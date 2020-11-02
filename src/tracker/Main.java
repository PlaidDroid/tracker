package tracker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

class Main {
	static int WIDTH = 600;
	static int HEIGHT = 400;
	static Dimension WINDOW_SIZE = new Dimension(WIDTH, HEIGHT);
	static Font FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
	static Color BACKGROUND = new Color(0);
	static Color FOREGROUND = new Color(0);

	private static JTabbedPane tabbedPane() throws Exception {
		JTabbedPane tabbedPane = new JTabbedPane();
//		tabbedPane.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
		tabbedPane.addTab("Overview", new OverviewPanel());
		tabbedPane.addTab("Insert", new InsertPanel());

		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		return tabbedPane;
	}

	private static void createAndShowGUI() throws Exception {
		JFrame frame = new JFrame("Tracker");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(WINDOW_SIZE);
		frame.setMinimumSize(WINDOW_SIZE);
		frame.setLocation(200, 200);
		frame.setResizable(false);
//		frame.setFont(font);
//		frame.getContentPane().setBackground(background);
//		frame.getContentPane().setForeground(foreground);

//		JLabel label = new JLabel("Hello");
//		frame.getContentPane().add(label);

		frame.setContentPane(tabbedPane());

		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) throws Exception {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					createAndShowGUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
