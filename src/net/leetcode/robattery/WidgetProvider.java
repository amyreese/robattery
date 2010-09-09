/**
 * 
 */
package net.leetcode.robattery;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

/**
 * @author jreese
 *
 */
public class WidgetProvider extends AppWidgetProvider {

	/**
	 * Widgets that need to be updated
	 */
	private static Map<Integer, Context> widgets = new HashMap<Integer, Context>();
	
	/**
	 * 
	 */
	public WidgetProvider() {
		// TODO Auto-generated constructor stub
	}
	
	public void onUpdate(Context context, AppWidgetManager manager, int[] ids) {
		for(int id : ids) {
			if(!widgets.containsKey(id)) {
				widgets.put(id, context);
			}
		}
	}

	public static void updateAll(Battery battery) {
		Set<Integer> keys = widgets.keySet();
		for(Integer id : keys) {
			Context context = widgets.get(id);
			AppWidgetManager manager = AppWidgetManager.getInstance(context);
			
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
			views.setTextViewText(R.id.widget_text, String.valueOf(battery.level) + "%");
			
			manager.updateAppWidget(id, views);
		}
	}

}
