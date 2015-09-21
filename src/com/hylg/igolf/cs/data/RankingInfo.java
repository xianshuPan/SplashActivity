package com.hylg.igolf.cs.data;

import java.io.Serializable;

public class RankingInfo implements Serializable {
	private static final long serialVersionUID = 8457574924520671856L;
	
	public long id;
	public String sn;
	public int rank;
	public String nickname;
	public String avatar;
	public double handicapIndex;
	public int matches;
	public int best;
	public String location;
	
	public String log() {
		return "RankingInfo: {\n" +
					" id: " + id + 
					" sn: " + sn + 
					" rank: " + rank + 
					" nickname: " + nickname +
					" avatar: " + avatar +
					" handicapIndex: " + handicapIndex +
					" matches: " + matches +
					" best: " + best + 
					" location: " + location;
	}
}
