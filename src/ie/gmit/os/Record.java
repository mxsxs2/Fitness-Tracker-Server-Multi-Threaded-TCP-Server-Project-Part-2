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
	 * Constructor using fields without record id
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
	 * Constructor using fields
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

	/**
	 * Constructor from a CSV line
	 * 
	 * @param recordCSVString
	 */
	public Record(String recordCSVString) {
		// If there is any lines
		if (recordCSVString.trim().length() > 0) {
			// Get the attributes of the string
			String[] attr = recordCSVString.trim().split(",");
			// Get the record id 
			if (attr.length > 0)
				this.recordId = Integer.parseInt(attr[0]);
			// Get the record type
			if (attr.length > 1)
				this.recordType = RecordType.valueOf(attr[1]);
			// Get the mode/type
			if (attr.length > 2)
				this.modeType = FitnessModeAndMealType.valueOf(attr[2]);
			// Get the content
			if (attr.length > 3)
				this.content = attr[3];
		}
	}

	public int getRecordId() {
		return this.recordId;
	}

	public void setRecordId(int recordId) {
		this.recordId = recordId;
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
