package nz.co.fortytwo.freeboard.zk;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Window;

public class InstrumentViewModel extends SelectorComposer<Window>{

	private static Logger logger = Logger.getLogger(InstrumentViewModel.class);
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@WireVariable
    private Session sess;
	
	@Wire ("#logg")
	private Panel logg;
	@Wire("#wind")
	private Panel wind;
	@Wire("#chartplotter")
	private Panel chartplotter;
	private double size = 400;
	
	
	public InstrumentViewModel() {
		super();
		logger.debug("Constructing..");
	}

	//@AfterCompose
	public void init() {
		logger.debug("Init..");
		//all hidden and in left corner
		if(sess.hasAttribute("size")){
			size=(Double) sess.getAttribute("sess");
			logger.debug("Size recovered from sess.."+size);
		}else{
			sess.setAttribute("sess", size);
		}
		logger.debug("Size = "+size);
		//logg.setFloatable(true);
		//wind.setFloatable(true);
		//chartplotter.setFloatable(true);
	}
	
	//@Listen("onClick = button#windShrink")
	public void windShrink(MouseEvent event) {
	    size=size-(size*0.1);
	    logger.debug(" shrinkSize = "+size);
	    wind.invalidate();
	}
	
	//@Listen("onClick = button#windGrow")
	public void windGrow(MouseEvent event) {
	    size=size+(size*0.1);
	    logger.debug(" growSize = "+size);
	    wind.setWidth(wind.getMinwidth()+"px");
	    wind.setHeight(wind.getMinheight()+"px");
	    wind.invalidate();
	}
	
	public double getSize() {
		if(sess.hasAttribute("size")){
			size=(Double) sess.getAttribute("sess");
			logger.debug("Size recovered from sess.."+size);
		}
		return size;
	}
	
	
	public void setSize(double size) {
		this.size = size;
		sess.setAttribute("sess", size);
	}

}
