package ariadne;

import ariadne.data.*;

public class Ariadne {
	public static void main(String[] args) {
		//System.out.println(System.getProperty("user.dir"));
		File fil = new File(new Descriptor("ABABABAABABABABABABABABABABABBAB\n3\n32\nBABABABABABABABABABABABABABABAB1\nBABABABABABABABABABABABABABABAB2\nBABABABABABABABABABABABABABABAB3\n".getBytes()), new BitMask(), "test");
		fil.getChunk(2);
		fil.setChunk(new Chunk("BABABABABABABABABABABABABABABAB3".getBytes()), 2);
		fil.getChunk(2);
		System.out.println("TROLOLOL");
		//System.out.println(Hexadecimal.fromString("BABABABABABABABABABABABABABABAB3"));
		/*String bla="ABABABABABABABABABABABABABABABAB\n";
		String alfa="70\n";
		String beta="40\n";
		String gamma="BABABABABABABABABABABABABABABAB1\nBABABABABABABABABABABABABABABAB2\nBABABABABABABABABABABABABABABAB3\n";
		bla+=alfa+beta+gamma;
		byte[] source = bla.getBytes();
		
<<<<<<< HEAD
		Descriptor blah = new Descriptor(source);
		System.out.println("HASH: "+new String(blah.getBytes()));
		System.out.println(blah.getBytes());*/
		
		/*final UI ui = new GraphicUI();

		ui.open();

		ui.onClosing(new Listener() {
			public void trigger(Event e) {
				ui.close();
			}
		});

		ui.onHashAdded(new Listener() {
=======
		final Server s = new Server(25566);
		s.start(4);
		
		final UI ui = new GraphicUI();
		ui.open();
		ui.onClosing(new Listener(){
			public void trigger(Event e) {
				ui.close();
				s.stop();
			}});
		
		ui.onHashAdded(new Listener(){
>>>>>>> 9473a312b21363f4d0b388f42d501580e8b0cfee
			public void trigger(Event e) {
				HashAddedEvent ev = (HashAddedEvent) e;
				System.out.println(ev.data);
			}
		});

		ui.eventLoop();
<<<<<<< HEAD

		ui.close();*/
	}
}
