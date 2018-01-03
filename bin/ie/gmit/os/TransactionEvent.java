package ie.gmit.os;
/**
 * User transaction types
 * @author Mxsxs2
 *
 */
public enum TransactionEvent {
	RegisterSuccess,
	RegisterFail,
	
	LoginFail,
	LoginSuccess,
	
	RecordAddSuccess,
	RecordAddFail,
	
	RecordLoaded,
	RecordListLoaded,
	
	RecordDeleteSuccess,
	RecordDeleteFail
}
