package playground.wrashid.PDES2;

public abstract class SelfhandleMessage extends Message {

	public abstract void selfhandleMessage();
	/*
	public void sendMessage(Scheduler scheduler,Message m, SimUnit targetUnit, double messageArrivalTime){
		m.sendingUnit=null;
		
		m.receivingUnit=targetUnit;
		
		m.setMessageArrivalTime(messageArrivalTime);
		scheduler.schedule(m);
	}
	*/
	public void recycleMessage() {
		MessageFactory.dispose(this);
	}

}
