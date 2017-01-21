package ticketingsystem;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class TicketingDS implements TicketingSystem
{
	//int globalTid = 0;
	AtomicLong globalTid=new AtomicLong(1);
	int routenum = 5;
	int coachnum = 8;
	int seatnum  = 100;
	int stationnum = 10;
	int[][][][] train;
	ReentrantLock[] lockArray;
	public void init()
	{
		//init train
		for (int i=0; i<=routenum; i++)
		{
			for(int j=0; j<=coachnum; j++)
			{
				for(int k=0; k<=seatnum; k++)
				{
					for(int l=0; l<=stationnum; l++)
						train[i][j][k][l] = 0;
				}
			}
		}
		
	}
	public TicketingDS()
	{
		train = new int[routenum+1][coachnum+1][coachnum+1][stationnum+1];
		init();
		lockArray = new ReentrantLock[routenum+1];
		for (int i=0; i<=routenum; i++)
		{
			lockArray[i] = new ReentrantLock();
		}
	}
	public TicketingDS(int _routenum, int _coachnum, int _seatnum, int _stationnum)
	{
		// init global var
		this.routenum = _routenum;
		this.coachnum = _coachnum;
		this.seatnum = _seatnum;
		this.stationnum = _stationnum;
		train = new int[_routenum+1][_coachnum+1][_seatnum+1][_stationnum+1];
		init();
		lockArray = new ReentrantLock[routenum+1];
		for (int i=0; i<=routenum; i++)
		{
			lockArray[i] = new ReentrantLock();
		}
	}

	//查询余票
	public int inquiry(int route, int start, int end) {
		if(route<1 || route>routenum ||
				start<1 || start>stationnum ||
				end<start ||end>stationnum)
					return 0;
		// TODO Auto-generated method stub
		lockArray[route].lock();
		try{
			int count=0;
			for(int coach=1; coach<=coachnum;coach++)
			{
				for(int seat=1; seat<=seatnum; seat++)
				{
					boolean flag = true;
					for(int i=start; i<end; i++)
					{
						if(train[route][coach][seat][i] != 0)
						{
							flag =false;
							break;
						}
					}
					if (flag)
						count++;
				}
			}
			return count;
		}
		finally
		{
			lockArray[route].unlock();
		}
	}

	//退票
	public boolean refundTicket(Ticket ticket) {

		if(ticket.tid<1 || ticket.tid>globalTid.get() ||
		ticket.route<1 || ticket.route>routenum ||
		ticket.coach<1 || ticket.coach>coachnum ||
		ticket.seat<1 || ticket.seat>seatnum ||
		ticket.departure<1 || ticket.departure>stationnum ||
		ticket.arrival<ticket.departure || ticket.arrival>stationnum)
			return false;
		// TODO Auto-generated method stub
		lockArray[ticket.route].lock();
		try{
			long myTid = ticket.tid;
			String myName = ticket.passenger;
			int myRoute = ticket.route;
			int myCoach = ticket.coach;
			int mySeat = ticket.seat;
			int myDeparture = ticket.departure;
			int myArrival = ticket.arrival;
			boolean flag = refundSeat(
					train[myRoute][myCoach][mySeat], myDeparture, myArrival, 
					(int)myTid);

			return flag;
		}
		finally{
			lockArray[ticket.route].unlock();
		}
	}

	//买票
	public Ticket buyTicket(String passenger, int route, int departure,
			int arrival) {
		if(route<1 || route>routenum ||
		departure<1 || departure>stationnum ||
		arrival<departure ||arrival>stationnum)
			return null;
		// TODO Auto-generated method stub
		Ticket ticket;
		lockArray[route].lock();
		try{
			int myTid = (int)globalTid.getAndIncrement();
			//搜索哪一个座位可以用
			for(int i=1; i<=coachnum; i++)
			{
				for(int j=1; j<=seatnum; j++)
				{
					boolean flag = saleSeat(train[route][i][j], 
											departure, arrival, (int)myTid);
					if (flag)
					{
						Ticket myTicket = new Ticket(myTid, passenger, route, i,
													j, departure, arrival);
						return myTicket;
					}
				}
			}
			return null;
		}
		finally{
			lockArray[route].unlock();
		}
	}
	public boolean saleSeat(int[] array, 
			int start, int end, int myTid)
	{
		//搜索该座位是否可以循环使用
		boolean flag = true;
		for(int i=start; i<end; i++)
		{
			if(array[i] != 0)
			{
				flag =false;
				break;
			}
		}
		//可以分配该座位，更新信息
		if(flag)
		{
			for(int i=start; i<end; i++)
			{
				array[i] = myTid;
			}
		}
		return flag;
	}
	
	public boolean refundSeat(int[] array, 
			int start, int end, int myTid)
	{
		boolean flag = true;
		//先查看是否票信息一致
		for(int i=start; i<end; i++)
		{
			if(array[i] != myTid)
			{
				flag = false;
				break;
			}
		
		}
		if(start>1)
		  {
		   if(array[start-1] == myTid)
		   {
		    flag = false;
		   }
		  
		  }
		  {
		   if(array[end] == myTid)
		   {
		    flag = false; 
		   }
		  }
		//可以退票
		if(flag)
		{
			//更新座位位置的信息
			for(int i=start; i<end; i++)
			{
				array[i] = 0;
			}
		}
		return flag;
	}
}
