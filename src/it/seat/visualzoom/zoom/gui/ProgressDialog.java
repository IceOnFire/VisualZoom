/*
 * Creato il 8-mag-2007
 */
package it.seat.visualzoom.zoom.gui;

import it.seat.visualzoom.zoom.data.ImageLoaderListener;
import it.seat.visualzoom.zoom.gui.event.ProgressDialogListener;
import it.seat.visualzoom.zoom.video.MovieWriterListener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * @author Leonardo Landini
 */
public class ProgressDialog extends JDialog implements ActionListener, ImageLoaderListener,
MovieWriterListener {
	private static final long serialVersionUID = 1L;

	private ArrayList<ProgressDialogListener> listeners;

	private JProgressBar progressBar;
	private JButton cancelButton;

	/**
	 * This is the default constructor
	 */
	public ProgressDialog() {
		super();
		listeners = new ArrayList<ProgressDialogListener>();
		setSize(250, 100);

		/* spacers */
		JLabel dxSpacer = new JLabel("");
		JLabel sxSpacer = new JLabel("");
		JLabel upSpacer = new JLabel("");
		dxSpacer.setPreferredSize(new java.awt.Dimension(10, 0));
		upSpacer.setPreferredSize(new java.awt.Dimension(0, 10));
		sxSpacer.setPreferredSize(new java.awt.Dimension(10, 0));
		add(upSpacer, java.awt.BorderLayout.NORTH);
		add(sxSpacer, java.awt.BorderLayout.WEST);
		add(dxSpacer, java.awt.BorderLayout.EAST);

		/* button panel */
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());

		/* button spacers */
		JPanel buttonUpSpacer = new JPanel();
		JPanel buttonDownSpacer = new JPanel();
		JPanel buttonSxSpacer = new JPanel();
		JPanel buttonDxSpacer = new JPanel();
		buttonSxSpacer.setPreferredSize(new Dimension(80, 10));
		buttonDxSpacer.setPreferredSize(new Dimension(80, 10));
		buttonPanel.add(buttonUpSpacer, BorderLayout.NORTH);
		buttonPanel.add(buttonDownSpacer, BorderLayout.SOUTH);
		buttonPanel.add(buttonSxSpacer, BorderLayout.WEST);
		buttonPanel.add(buttonDxSpacer, BorderLayout.EAST);

		/* cancel button */
		cancelButton = new JButton();
		cancelButton.setText("Annulla");
		cancelButton.addActionListener(this);
		buttonPanel.add(cancelButton, BorderLayout.CENTER);

		add(buttonPanel, BorderLayout.SOUTH);

		/* progress bar */
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		add(progressBar, BorderLayout.CENTER);
	}

	public void setProgress(int progress) {
		progressBar.setValue(progress);
	}

	public int getProgress() {
		return progressBar.getValue();
	}

	public void setStringPainted(boolean painted){
		progressBar.setStringPainted(painted);
	}

	public void addListener(ProgressDialogListener listener) {
		listeners.add(listener);
	}

	private void fireOperationCancelledEvent() {
		for (ProgressDialogListener listener : listeners) {
			listener.operationCancelled();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// int result =
		// JOptionPane.showInternalConfirmDialog(ProgressDialog.this,"Attenzione","Annullare
		// l'operazione in corso ?",JOptionPane.YES_NO_OPTION);
		// if(result == JOptionPane.OK_OPTION){
		// //fireOperationCancelledEvent();
		// ProgressDialog.this.dispose();
		// }
		fireOperationCancelledEvent();
		dispose();
	}

	/** + ImageLoaderListener + */
	@Override
	public void onError(int imageNumber, Exception e) {
		dispose();
	}

	@Override
	public void onImageLoaded(int imageNumber, int totImages) {
		int prog = getProgress();
		prog += 100 / totImages;
		setProgress(prog);
	}

	@Override
	public void onImageLoaded(int imageNumber, BufferedImage img) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoadingCancelled() {
		dispose();
	}

	@Override
	public void onLoadingComplete() {
		dispose();
	}
	/** - ImageLoaderListener - */

	/** + MovieWriterListener + */
	@Override
	public void onError(Exception e) {
		// do nothing
	}

	@Override
	public void onProgress(int perc) {
		setProgress(perc);
	}

	@Override
	public void onWriteComplete() {
		dispose();
	}
	/** - MovieWriterListener - */
}
