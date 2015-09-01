package com.jboss.devstudio.core.installer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import junit.framework.TestCase;

import com.jboss.devstudio.core.installer.ServerListPanel.ServerListTableModel;
import com.jboss.devstudio.core.installer.bean.RuntimePath;
import com.jboss.devstudio.core.installer.bean.ServerListBean;

public class ServerListTableModelTest extends TestCase {

	public void testGetValueAt() {
		ServerListBean bean = new ServerListBean();
		bean.setServers(new ArrayList<RuntimePath>(ServerListBeanTestData.getTestList()));
		ServerListTableModel model = new ServerListTableModel(bean, CommonTestData.langpack);
		for (RuntimePath serBean : bean.getServers()) {
			int index = bean.getServers().indexOf(serBean);
			assertEquals(serBean.getLocation(), model.getValueAt(index, 1));
			assertEquals(Boolean.valueOf(serBean.isScannedOnStartup()), model.getValueAt(index, 0));
		}
		RuntimePath updateBean = new RuntimePath("/home/user/server4",true);
		TestTableModelListener testListener = new TestTableModelListener(model, TableModelEvent.UPDATE,1, 1, TableModelEvent.ALL_COLUMNS);
		model.addTableModelListener(testListener);
		bean.setServers(1, updateBean);
		testListener.assertPassed();
		testListener.reset();
		int index = 1;
		assertEquals(model.getRowCount(), bean.getServers().size());
		assertEquals(updateBean.getLocation(), model.getValueAt(index, 1));
		assertEquals(Boolean.valueOf(updateBean.isScannedOnStartup()), model.getValueAt(index, 0));
		List<RuntimePath> newServers = new ArrayList<RuntimePath>(Arrays.asList(new RuntimePath("/home/user/server5",true)));
		testListener = new TestTableModelListener(model, TableModelEvent.UPDATE, 0, Integer.MAX_VALUE, TableModelEvent.ALL_COLUMNS);
		model.addTableModelListener(testListener);
		bean.setServers(newServers);
		testListener.assertPassed();
		testListener.reset();
		
		bean.remove(0);
		testListener.assertPassed();
		assertEquals(model.getRowCount(),0);
	}
	
	public static class TestTableModelListener implements TableModelListener {

		private TableModel model;
	    protected int type;
	    protected int firstRow;
	    protected int lastRow;
	    protected int column;
	    
	    boolean passed = false;
	    
	    public TestTableModelListener(TableModel model, int type, int firstRow,
				int lastRow, int column) {
			super();
			this.model = model;
			this.type = type;
			this.firstRow = firstRow;
			this.lastRow = lastRow;
			this.column = column;
		}
	    
		public void tableChanged(TableModelEvent e) {
			if(!passed 
					&& e.getSource()==model
					&& e.getType() == type
					&& e.getFirstRow() == firstRow
					&& e.getLastRow() == lastRow 
					&& e.getColumn() == column) {
				passed = true;
			}
		}
		
		public void assertPassed() {
			assertTrue(passed);
		}
		
		public void reset() {
			passed = false;
		}
	}

}
