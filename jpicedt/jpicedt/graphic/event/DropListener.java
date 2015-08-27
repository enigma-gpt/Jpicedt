package jpicedt.graphic.event;

import java.awt.Frame;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jpicedt.JPicEdt;

public class DropListener implements DropTargetListener {

	public DropListener(Frame frame) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
		dtde.getDropAction();

	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dragExit(DropTargetEvent dte) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drop(DropTargetDropEvent dtde) {
		dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

		try {
			Transferable t = dtde.getTransferable(); 

			if(t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				List<File> fileList = (java.util.List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);

				for(File file : fileList) {
					String path = file.getPath();
					JPicEdt.openBoard(path);
				}
			}
			else if(t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String path = (String) t.getTransferData(DataFlavor.stringFlavor);

				if(path.contains("file://")) {

					List<String> filePaths = null;

					// decode characters code like %20 (whitespace)
					path = URLDecoder.decode(path, "UTF-8");

					path = path.replace("file://", "");
					path = path.replaceAll("\\s+$", "");

					filePaths = Arrays.asList(path.split("\\r?\\n"));

					for(String currentPath : filePaths) {
						JPicEdt.openBoard(currentPath);
					}
				}
			}
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}