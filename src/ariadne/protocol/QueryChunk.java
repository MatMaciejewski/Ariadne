package ariadne.protocol;

import java.nio.ByteBuffer;

import ariadne.data.Catalogue;
import ariadne.data.Database;
import ariadne.data.File;
import ariadne.data.Hash;
import ariadne.net.Port;

/*
 * CHUNK query
 * 
 * Byte 0		- query code
 * Byte 1-2		- client port (server is gonna save it along with our IP)
 * Byte 3-18	- chased hash 
 * Byte 19-22	- chunk id
 */

public class QueryChunk extends PortHashQuery {

	public int getChunkId(){
		return getByteBuffer().getInt(19);
	}

	@Override
	public byte getCode() {
		return 2;
	}
	
	@Override
	public int expectedLength(){
		return 1 + Port.BYTESIZE + Hash.LENGTH + 4; // 23
	}
	
	public static QueryChunk prepare(Port port, Hash hash, int id){
		QueryChunk q = new QueryChunk();
		PortHashQuery.prepare(q, port, hash);
		ByteBuffer b = q.accessByteBuffer();
		b.putInt(19, id);
		return q;
	}

	@Override
	public Response respond() {
		if(getAuthor() != null){
			Catalogue.addPeer(getHash(), getAuthor(), Catalogue.DEF_TIMEOUT, false);
		}
		ResponseChunk r;
		
		File f = Database.getFile(getHash());
		if(f == null){
			r = ResponseChunk.prepare(null);
		}else{
			r = ResponseChunk.prepare(f.getChunk(getChunkId()));
		}
		return r;
	}
}
