import java.util.ArrayList;


public class SequencePair {
		
		public ArrayList<String> lrcSeq;
		public ArrayList<String> meloSeq;
		//public int size;
		
		public SequencePair(ArrayList<String> lrcSeq, ArrayList<String> meloSeq) {
			this.lrcSeq = lrcSeq;
			this.meloSeq = meloSeq;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((lrcSeq == null) ? 0 : lrcSeq.hashCode());
			result = prime * result
					+ ((meloSeq == null) ? 0 : meloSeq.hashCode());
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
			SequencePair other = (SequencePair) obj;
			if (lrcSeq == null) {
				if (other.lrcSeq != null)
					return false;
			} else if (!lrcSeq.equals(other.lrcSeq))
				return false;
			if (meloSeq == null) {
				if (other.meloSeq != null)
					return false;
			} else if (!meloSeq.equals(other.meloSeq))
				return false;
			return true;
		}

		
	}
	