package ticketingsystem;

//ticket class
class Ticket{
	long tid;
	String passenger;
	int route;
	int coach;
	int seat;
	int departure;
	int arrival;
	public Ticket(long _tid, String _passenger, int _route, int _coach,
			int _seat, int _departure, int _arrival)
	{
		tid = _tid;
		passenger = _passenger;
		route = _route;
		coach = _coach;
		seat = _seat;
		departure = _departure;
		arrival = _arrival;
	}
	public Ticket(){};
	
	public String toString(){
		String str = "Tid="+tid+",Passenger="+passenger+",Route="
							+route+",Coach="+coach+",Seat="
							+seat+",Departure="+departure+",Arrival="
							+arrival;
		return str;
	}
	
}


public interface TicketingSystem
{
	Ticket buyTicket(String passenger , int route ,
			int departure , int arrival ) ;
	int inquiry ( int route , int departure , int arrival) ;
	boolean refundTicket(Ticket ticket ) ;
}
