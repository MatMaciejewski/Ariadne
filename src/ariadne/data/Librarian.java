package ariadne.data;

public class Librarian extends Thread {
	private boolean loop;
	@Override
	public void run(){
		loop = true;
		
		while(loop){
			
			Catalogue.update();
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("Librarian thread interrupted");
			}
			
		}
		
	}
	
	public void halt(){
		loop = false;
	}
}
