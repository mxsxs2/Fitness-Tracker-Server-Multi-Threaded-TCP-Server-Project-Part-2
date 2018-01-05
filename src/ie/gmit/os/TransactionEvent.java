package ie.gmit.os;
/**
 * Transaction event types
 * @author Mxsxs2
 *
 */
public enum TransactionEvent {
	ConnectionError,
	ConnectionStarted,
	ConnectionEnded,
	
	RegisterSuccess,
	RegisterFail,
	
	LoginSuccess,
	LoginFail,
	
	RecordLoaded,
	RecordAddSuccess,
	RecordAddFail,
	RecordDeleteSuccess,
	RecordDeleteFail,
	RecordListLoaded
	
}
