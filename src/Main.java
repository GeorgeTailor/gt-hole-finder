import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.george.tailor.initializer.LinksInitializer;

public class Main {

	public static void main(String args[]) {
		new Main().start();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void start() {
		String[] items = {"login", "upload"};
		JComboBox combo = new JComboBox(items);
		JTextField field1 = new JTextField("http://localhost:81/bricks");
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(new JLabel("Tested vulnerability:"));
		panel.add(combo);
		panel.add(new JLabel("Target:"));
		panel.add(field1);
		int result = JOptionPane.showConfirmDialog(null, panel, "Security Test", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			LinksInitializer linksInitializer = new LinksInitializer();
			linksInitializer.init(field1.getText(), combo.getSelectedIndex());
		} else {
			System.out.println("Cancelled");
		}
	}
}
