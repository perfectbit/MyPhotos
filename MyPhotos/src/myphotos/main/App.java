package myphotos.main;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class App {
	private static MainFrame mainFrame;
	private static DBProvider dbprovider;
	private static Model model;
	private static AppProperties prop;

    public static void main(String[] args) {
    	try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
    	prop = new AppProperties();
    	dbprovider = new DBProvider();
    	model = new Model(dbprovider);    	
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }    
    
	private static void createAndShowGUI() {
		mainFrame = new MainFrame();		
	}

	public static MainFrame getMainFrame() {
		return mainFrame;
	}
	
	public static String getProp(String key) {
		return prop.getPropertie(key);
	}
	
	public static Model getModel() {
		return model;
	}
}