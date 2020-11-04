package tracker;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

class OverviewPanel extends JPanel {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	OverviewPanel() throws Exception {
		// settings
		this.setFont(Main.FONT);
		// this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		this.setPreferredSize(Main.WINDOW_SIZE);

		/* filterPanel */
		/*
		 * TODO: 1) Fix yearPicker - it throws an error when changing year; possibly
		 * because of concurrent action listener triggers
		 */

		// main
		final JPanel filterPanel = new JPanel();
		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.X_AXIS));

		// section left
		final JPanel datePanel = new JPanel();
		datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.X_AXIS));

		final LocalDateTime currentDateTime = LocalDateTime.now();
		final int currentMonth = currentDateTime.getMonthValue();
		final int currentYear = currentDateTime.getYear();
		// years
		final JComboBox<Year> yearPicker = new JComboBox<Year>();
		yearPicker.setModel(new DefaultComboBoxModel(setYears()));
		yearPicker.setSelectedIndex(yearPicker.getModel().getSize() - 1);

		// months
		final JComboBox<Month> monthPicker = new JComboBox<Month>();
		monthPicker.setModel(new DefaultComboBoxModel(setMonths(true)));
		monthPicker.setSelectedIndex(monthPicker.getModel().getSize() - 1);

		// day
		final JComboBox<Integer> dayPicker = new JComboBox<Integer>();
		dayPicker.setModel(new DefaultComboBoxModel(
				setDays(Month.of(currentMonth), ((Year) yearPicker.getSelectedItem()).isLeap(), true)));
		dayPicker.setSelectedIndex(dayPicker.getModel().getSize() - 1);

		// action listeners
		yearPicker.addItemListener(e -> {
			monthPicker.setModel(new DefaultComboBoxModel(setMonths(((Year) e.getItem()).getValue() == currentYear)));
		});

		monthPicker.addItemListener(e -> {
			Boolean b = (((Year) yearPicker.getSelectedItem()).getValue() == currentYear)
					&& (((Month) e.getItem()).getValue() == currentMonth);
			dayPicker.setModel(new DefaultComboBoxModel(
					setDays((Month) e.getItem(), ((Year) yearPicker.getSelectedItem()).isLeap(), b)));

		});

		datePanel.add(yearPicker);
		datePanel.add(monthPicker);
		datePanel.add(dayPicker);

		filterPanel.add(datePanel);

		/* metricPanel */
		/*
		 * TODO: Add listener so that the UI is updated to show right info
		 */

		DBHelper helper = new DBHelper();
		helper.printAll();

		LocalDate date = getDate(datePanel);
		HashMap<String, Integer> data = helper.getDataFor(date);

		JPanel metricPanel = new JPanel();
		metricPanel.setLayout(new BoxLayout(metricPanel, BoxLayout.Y_AXIS));

		final String[] panelLabels = { "Audio", "Visual", "Research", "Work" };
		for (int i = 0; i < panelLabels.length; i++) {
			JPanel panel = generateLabelAndProgressBar(panelLabels[i]);
			metricPanel.add(panel);
		}
		for (Component panel : metricPanel.getComponents()) {
			JLabel lab = (JLabel) ((JPanel) panel).getComponent(0);
			JProgressBar bar = (JProgressBar) ((JPanel) panel).getComponent(2);
			if (data != null) {
				bar.setValue(data.get(lab.getText()));
			} else {
				bar.setValue(0);
			}
		}

		this.add(filterPanel, BorderLayout.NORTH);
		this.add(metricPanel, BorderLayout.CENTER);
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

	private Year[] setYears() {
		final int currentYear = Year.now().getValue();
		final int yearStart = 2000;
		final int yearEnd = currentYear;
		Year[] years = new Year[yearEnd - yearStart + 1];
		IntStream.rangeClosed(yearStart, yearEnd).forEach(i -> years[i - yearStart] = Year.of(i));
		return years;
	}

	private Month[] setMonths(Boolean isTillCurrentMonth) {
		int currentMonth = LocalDate.now().getMonth().getValue();
		if (isTillCurrentMonth) {
			Month[] months = new Month[currentMonth];
			for (int i = 0; i < months.length; i++) {
				if (i == currentMonth) {
					break;
				}
				months[i] = Month.values()[i];
			}
			return months;
		}
		return Month.values();
	}

	private Integer[] setDays(Month month, Boolean isLeapYear, Boolean isTillCurrentDay) {
		int currentDay = LocalDateTime.now().getDayOfMonth();
		int noOfDays = month.length(isLeapYear);
		if (isTillCurrentDay) {
			return Arrays.stream(IntStream.rangeClosed(1, currentDay).toArray()).boxed().toArray(Integer[]::new);
		}
		return Arrays.stream(IntStream.rangeClosed(1, noOfDays).toArray()).boxed().toArray(Integer[]::new);
	}

	private Month getMonth(int index) {
		return Month.values()[index];
	}

	@SuppressWarnings("unchecked")
	private LocalDate getDate(JPanel datePanel) {
		JComboBox<Year> yearPicker = (JComboBox<Year>) datePanel.getComponents()[0];
		JComboBox<String> monthPicker = (JComboBox<String>) datePanel.getComponents()[1];
		JComboBox<Integer> datePicker = (JComboBox<Integer>) datePanel.getComponents()[2];
		int year = ((Year) yearPicker.getSelectedItem()).getValue();
		int month = monthPicker.getSelectedIndex() + 1;
		int day = ((Integer) datePicker.getSelectedItem()).intValue();
		return LocalDate.of(year, month, day);
	}
}
