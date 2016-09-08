package net.anotheria.moskito.webui.dashboards.action;

import net.anotheria.anoplass.api.APIException;
import net.anotheria.maf.action.ActionCommand;
import net.anotheria.maf.action.ActionMapping;
import net.anotheria.maf.bean.FormBean;
import net.anotheria.moskito.core.config.dashboards.DashboardConfig;
import net.anotheria.moskito.webui.dashboards.api.DashboardAO;
import net.anotheria.moskito.webui.dashboards.api.DashboardChartAO;
import net.anotheria.moskito.webui.gauges.api.GaugeAO;
import net.anotheria.moskito.webui.gauges.bean.GaugeBean;
import net.anotheria.moskito.webui.threshold.api.ThresholdStatusAO;
import net.anotheria.moskito.webui.threshold.bean.ThresholdStatusBean;
import net.anotheria.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * This action renders a dashboard. If no dashboard is selected explicitly the first dashboard is taken.
 *
 * @author lrosenberg
 * @since 12.02.15 14:02
 */
public class ShowDashboardAction extends BaseDashboardAction {

	enum LastOperation {
		dcr, //dashboard create
		drm, // dashboard remove
		gadd, // add gauge to dashboard
		grm, // remove gauge from dashboard
		tadd, // add threshold to dashboard
		trm, // remove threshold from dashboard
	}

	@Override
	public ActionCommand execute(ActionMapping actionMapping, FormBean formBean, HttpServletRequest request, HttpServletResponse response) throws Exception {

		String dashboardName = getSelectedDashboard(request);
		Boolean gaugesPresent = false;
		Boolean chartsPresent = false;
		Boolean thresholdsPresent = false;

		//set default values, allow to exit previously.
		request.setAttribute("gaugesPresent", gaugesPresent);
		request.setAttribute("chartsPresent", chartsPresent);
		request.setAttribute("thresholdsPresent", thresholdsPresent);
		request.setAttribute("showHelp", !(gaugesPresent || chartsPresent || thresholdsPresent));

		DashboardConfig selectedDashboard = getDashboardAPI().getDashboardConfig(dashboardName);
		if (dashboardName != null && selectedDashboard == null) {
			dashboardName = null;
		}

		List<ThresholdStatusBean> thresholdStatusAOList;
		List<GaugeBean> gaugeAOList;
		List<DashboardChartAO> dashboardChartAOList;

		if (dashboardName == null) {
			thresholdStatusAOList = getThresholdBeans(getThresholdAPI().getThresholdStatuses());
			gaugeAOList = getGaugeBeans(getGaugeAPI().getGauges());
		} else {
			DashboardAO dashboard = getDashboardAPI().getDashboard(dashboardName);
			thresholdStatusAOList = getThresholdBeans(dashboard.getThresholds());
			gaugeAOList = getGaugeBeans(dashboard.getGauges());
		}

		//now we definitely have a selected dashboard.
		//prepare thresholds
		if (thresholdStatusAOList!=null && thresholdStatusAOList.size()>0){
			request.setAttribute("thresholds", thresholdStatusAOList);
			thresholdsPresent = true;
		}

		//prepare gauges
		if (gaugeAOList!=null && gaugeAOList.size()>0){
			request.setAttribute("gauges", gaugeAOList);
			gaugesPresent = true;
		}

		//prepare charts
		DashboardAO dashboard = getDashboardAPI().getDashboard(getDashboardAPI().getDefaultDashboardName());
		if (dashboard.getCharts()!=null && dashboard.getCharts().size()>0){
			request.setAttribute("charts", dashboard.getCharts());
			chartsPresent = true;
		}

		//maybe the value has changed.
		request.setAttribute("gaugesPresent", gaugesPresent);
		request.setAttribute("chartsPresent", chartsPresent);
		request.setAttribute("thresholdsPresent", thresholdsPresent);
		request.setAttribute("showHelp", !(gaugesPresent || chartsPresent || thresholdsPresent));

		addInfoMessage(request);

		return actionMapping.success();
	}

	@Override
	protected String getPageName() {
		return "dashboard";
	}

	private List<GaugeBean> getGaugeBeans(List<GaugeAO> gaugeAOList) throws APIException {
		List<GaugeBean> ret = new ArrayList<>();
		if (gaugeAOList == null || gaugeAOList.size() == 0)
			return ret;

		List<DashboardAO> dashboardAOList = new ArrayList<>();
		for(String name : getDashboardAPI().getDashboardNames()) {
			dashboardAOList.add(getDashboardAPI().getDashboard(name));
		}
		for (GaugeAO gaugeAO : gaugeAOList) {
			String dashboardNames = "";
			for(DashboardAO dashboardAO: dashboardAOList) {
				if (dashboardAO.getGauges() == null || !dashboardAO.getGauges().contains(gaugeAO)) {
					dashboardNames += dashboardAO.getName()+",";
				}
			}
			if (dashboardNames.length() > 0)
				dashboardNames = dashboardNames.substring(0, dashboardNames.length()-1);
			ret.add(new GaugeBean(gaugeAO, dashboardNames));
		}

		return ret;
	}

	private List<ThresholdStatusBean> getThresholdBeans(List<ThresholdStatusAO> thresholdStatusAOList) throws APIException {
		List<ThresholdStatusBean> ret = new ArrayList<>();
		if (thresholdStatusAOList == null || thresholdStatusAOList.size() == 0)
			return ret;

		List<DashboardAO> dashboardAOList = new ArrayList<>();
		for(String name : getDashboardAPI().getDashboardNames()) {
			dashboardAOList.add(getDashboardAPI().getDashboard(name));
		}
		for (ThresholdStatusAO thresholdStatusAO : thresholdStatusAOList) {
			String dashboardNames = "";
			for(DashboardAO dashboardAO: dashboardAOList) {
				if (dashboardAO.getThresholds() == null || !dashboardAO.getThresholds().contains(thresholdStatusAO)) {
					dashboardNames += dashboardAO.getName()+",";
				}
			}
			if (dashboardNames.length() > 0)
				dashboardNames = dashboardNames.substring(0, dashboardNames.length()-1);
			ret.add(new ThresholdStatusBean(thresholdStatusAO, dashboardNames));
		}

		return ret;
	}

	private void addInfoMessage(HttpServletRequest request) {
		String lastOperation = request.getParameter("lo");

		if (lastOperation == null)
			return;
		String dashboardName = request.getParameter("dashboard");
		LastOperation lo = LastOperation.valueOf(lastOperation);
		String infoMessage = "";
		switch (lo) {
			case dcr:
				infoMessage = "Dashboard \"" + dashboardName + "\" has been created";
				break;
			case drm:
				infoMessage = "Dashboard \"" + dashboardName + "\" has been deleted";
				break;
			case gadd:
				infoMessage = "Gauge has been added to selected dashboards";
				break;
			case grm:
				infoMessage = "Gauge has been removed from dashboard";
				break;
			case tadd:
				infoMessage = "Threshold has been added to dashboard";
				break;
			case trm:
				infoMessage = "Threshold has been removed from dashboard";
				break;
			default:
				break;
		}

		if (!StringUtils.isEmpty(infoMessage))
			request.setAttribute("infoMessage", infoMessage);
	}
}
