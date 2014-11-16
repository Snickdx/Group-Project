package clientutils;

public class ServerResp<T>
{
	
	private T item;
	private Status stat;
	
	public ServerResp(T item, Status stat)
	{
		this.item = item;
		this.stat = stat;
	}

	public T getItem() {
		return item;
	}

	public Status getStat() {
		return stat;
	}
	
	
}
