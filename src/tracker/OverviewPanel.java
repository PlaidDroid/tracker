package tracker;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

class OverviewPanel extends JPanel {
	OverviewPanel() throws Exception {
		// settings
		this.setFont(Main.FONT);
		// this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		this.setPreferredSize(Main.WINDOW_SIZE);

		// metricPanel

		this.add(filterPanel(), BorderLayout.NORTH);
		this.add(metricPanel(), BorderLayout.CENTER);
	}

	private JPanel generateLabelAndProgressBar(String name) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		JLabel label = new JLabel(name);

		JProgressBar prog = new JProgressBar();
		int progWidth = (int) (Main.WIDTH - (Main.WIDTH * 0.6));
		int progHeight = 10;
		Dimension progDimen = new Dimension(progWidth, progHeight);
		prog.setMaximumSize(progDimen);

		panel.add(label);
		panel.add(Box.createRigidArea(new Dimension(15, 0)));
		panel.add(prog);

		return panel;
	}

	private JPanel metricPanel() throws Exception {
		DBHelper helper = new DBHelper();
		helper.printAll();

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		final String[] panelLabels = { "Audio", "Visual", "Research", "Work" };
		for (int i = 0; i < panelLabels.length; i++) {
			panel.add(generateLabelAndProgressBar(panelLabels[i]));
		}

		return panel;
	}

	private JPanel filterPanel() {
		/*
		 * TOFIX: 1) Fix yearPicker - it throws an error when changing year; possibly
		 * because of concurrent action listener triggers
		 */

		// main
		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

		// section left
		final JPanel datePanel = new JPanel();

		final LocalDateTime currentDateTime = LocalDateTime.now();
		final int currentMonth = currentDateTime.getMonthValue();
		final int currentYear = currentDateTime.getYear();
		// years
		final JComboBox<Year> yearPicker = new JComboBox<Year>();
		setYears(yearPicker);
		yearPicker.setEnabled(false);

		// months
		final JComboBox<String> monthPicker = new JComboBox<String>();
		setMonths(monthPicker, true);

		// day
		final JComboBox<Integer> dayPicker = new JComboBox<Integer>();
		setDays(dayPicker, getMonth(monthPicker.getSelectedIndex()), Year.now().isLeap(), true);

		// action listeners
		ActionListener onYearChange = e -> {
			if (((Year) yearPicker.getSelectedItem()).getValue() == currentYear) {
				setMonths(monthPicker, true);
			} else {
				setMonths(monthPicker, false);
			}
		};
		ActionListener onMonthChange = e -> {
			if ((monthPicker.getSelectedIndex() == currentMonth - 1)
					&& ((Year) yearPicker.getSelectedItem()).getValue() == currentYear) {
				setDays(dayPicker, getMonth(monthPicker.getSelectedIndex()), Year.now().isLeap(), true);
			} else {
				setDays(dayPicker, getMonth(monthPicker.getSelectedIndex()), Year.now().isLeap(), false);
			}
		};

		yearPicker.addActionListener(onYearChange);
		monthPicker.addActionListener(onMonthChange);

		mainPanel.add(yearPicker);
		mainPanel.add(monthPicker);
		mainPanel.add(dayPicker);

		return mainPanel;
	}

	private void setYears(JComboBox<Year> yearPicker) {
		final int currentYear = Year.now().getValue();
		final int yearStart = 2000;
		final int yearEnd = currentYear;
		IntStream.rangeClosed(yearStart, yearEnd).forEach(i -> yearPicker.addItem(Year.of(i)));
		yearPicker.setSelectedItem(Year.of(currentYear));

	}

	private void setMonths(JComboBox<String> monthPicker, Boolean isTillCurrentMonth) {
		int currentMonth = LocalDateTime.now().getMonth().getValue();
		if (isTillCurrentMonth) {
			if (monthPicker.getItemCount() > 0) {
				monthPicker.removeAllItems();
			}
			for (Month month : getMonths()) {
				if (month.getValue() == currentMonth + 1) {
					break;
				}
				monthPicker.addItem(month.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
			}
			monthPicker.setSelectedIndex(currentMonth - 1);
		} else {
			if (monthPicker.getItemCount() > 0) {
				monthPicker.removeAllItems();
			}
			for (Month month : getMonths()) {
				monthPicker.addItem(month.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
			}
			monthPicker.setSelectedIndex(0);
		}
	}

	private void setDays(JComboBox<Integer> dayPicker, Month month, Boolean isLeapYear, Boolean isTillCurrentDay) {
		int currentDay = LocalDateTime.now().getDayOfMonth();
		int noOfDays = month.length(isLeapYear);
		if (dayPicker.getItemCount() > 0) {
			dayPicker.removeAllItems();
		}
		if (isTillCurrentDay) {
			IntStream.rangeClosed(1, noOfDays).forEach(i -> {
				if (i <= currentDay) {
					dayPicker.addItem(i);
				}
			});
			dayPicker.setSelectedItem(currentDay);
		} else {
			IntStream.rangeClosed(1, noOfDays).forEach(i -> dayPicker.addItem(i));
			dayPicker.setSelectedItem(0);
		}
	}

	private Month[] getMonths() {
		return Month.values();
	}

	private Month getMonth(int index) {
		return Month.values()[index];
	}

}
