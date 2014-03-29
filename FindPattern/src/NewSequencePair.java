
import java.util.ArrayList;


public class NewSequencePair {
		
		@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((durSeq == null) ? 0 : durSeq.hashCode());
		result = prime * result + ((meloSeq == null) ? 0 : meloSeq.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NewSequencePair other = (NewSequencePair) obj;
		if (durSeq == null) {
			if (other.durSeq != null)
				return false;
		} else if (!durSeq.equals(other.durSeq))
			return false;
		if (meloSeq == null) {
			if (other.meloSeq != null)
				return false;
		} else if (!meloSeq.equals(other.meloSeq))
			return false;
		return true;
	}

		public ArrayList<String> meloSeq;
		public ArrayList<String> durSeq;
		//public int size;
		
		public NewSequencePair(ArrayList<String> meloSeq, ArrayList<String> durSeq) {
			this.meloSeq = meloSeq;
			this.durSeq = durSeq;
		}
		
		

		
	}
	