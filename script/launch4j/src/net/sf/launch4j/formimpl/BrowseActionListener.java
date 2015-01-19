/*
	Launch4j (http://launch4j.sourceforge.net/)
	Cross-platform Java application wrapper for creating Windows native executables.

	Copyright (C) 2004, 2006 Grzegorz Kowal

	This program is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

/*
 * Created on May 1, 2006
 */
package net.sf.launch4j.formimpl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JTextField;

import net.sf.launch4j.FileChooserFilter;

/**
 * @author Copyright (C) 2006 Grzegorz Kowal
 */
public class BrowseActionListener implements ActionListener {
	private final boolean _save;
	private final JFileChooser _fileChooser;
	private final FileChooserFilter _filter;
	private final JTextField _field;

	public BrowseActionListener(boolean save, JFileChooser fileChooser,
			FileChooserFilter filter, JTextField field) {
		_save = save;
		_fileChooser = fileChooser;
		_filter = filter;
		_field = field;
	}

	public void actionPerformed(ActionEvent e) {
		if (!_field.isEnabled()) {
			return;
		}
		_fileChooser.setFileFilter(_filter);
		_fileChooser.setSelectedFile(new File(""));
		int result = _save
				? _fileChooser.showSaveDialog(MainFrame.getInstance())
				: _fileChooser.showOpenDialog(MainFrame.getInstance());
		if (result == JFileChooser.APPROVE_OPTION) {
			_field.setText(_fileChooser.getSelectedFile().getPath());
		}
		_fileChooser.removeChoosableFileFilter(_filter);
	}
}