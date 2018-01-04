package ie.gmit.os;

public class Record {
	// The id of this record
	private int recordId;
	// The type of this record
	private RecordType recordType;
	// The fitness mode or meal type
	private FitnessModeAndMealType modeType;
	// The content of the record. Either description or duration
	private String content;
	// The maximum length of the content
	private int contentLength;

	/**
	 * Constructor usig fileds without record id
	 * 
	 * @param recordType
	 * @param modeType
	 * @param content
	 */
	public Record(RecordType recordType, FitnessModeAndMealType modeType, String content) {
		super();
		this.recordType = recordType;
		this.modeType = modeType;
		this.content = content;
	}

	/**
	 * Constructor usig fileds
	 * 
	 * @param recordId
	 * @param recordType
	 * @param modeType
	 * @param content
	 */
	public Record(int recordId, RecordType recordType, FitnessModeAndMealType modeType, String content) {
		super();
		this.recordId = recordId;
		this.recordType = recordType;
		this.modeType = modeType;
		this.content = content;
	}


	public int getRecordId() {
		return this.recordId;
	}

	public String getContent() {
		return this.content;
	}

	/**
	 * Set the record content. If the content length is higher than the allowed, it
	 * is cut.
	 * 
	 * @param content
	 */
	public void setContent(String content) {
		// If the content length is higher than the allowed
		if (content.length() > this.contentLength) {
			// Cut the input string and save
			this.content = content.substring(0, this.contentLength - 1);
		} else {
			// Save string
			this.content = content;
		}
	}

	public RecordType getRecordType() {
		return this.recordType;
	}

	public FitnessModeAndMealType getModeType() {
		return this.modeType;
	}

	/**
	 * Converts the record details to CSV format
	 * 
	 * @return String
	 */
	public String toCSVString() {
		return recordType + "," + modeType + "," + content;
	}

	@Override
	public String toString() {
		return "Record [recordId=" + recordId + ", recordType=" + recordType + ", modeType=" + modeType + ", content="
				+ content + "]";
	}

}
