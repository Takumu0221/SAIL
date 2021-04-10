import java.util.List;

import genius.core.AgentID;
import genius.core.Bid;
import genius.core.actions.Accept;
import genius.core.actions.Action;
import genius.core.actions.EndNegotiation;
import genius.core.actions.Offer;
import genius.core.parties.AbstractNegotiationParty;
import genius.core.parties.NegotiationInfo;
import genius.core.utility.AbstractUtilitySpace;


/**
 * A simple example agent that makes random bids above a minimum target utility.
 *
 * @author Takumu Shimizu
 */
public class Agent1 extends AbstractNegotiationParty
{
	private static double MINIMUM_TARGET = 0.85;
	private Bid lastOffer;

	/**
	 * Initializes a new instance of the agent.
	 */
	@Override
	public void init(NegotiationInfo info)
	{
		super.init(info);
	}

	/**
	 * Makes a random offer above the minimum utility target
	 * Accepts everything above the reservation value at the very end of the negotiation; or breaks off otherwise.
	 */
	@Override
	public Action chooseAction(List<Class<? extends Action>> possibleActions)
	{
		// Check for acceptance if we have received an offer
		if (lastOffer != null)
			if (timeline.getTime() >= 0.99)
				if (getUtility(lastOffer) >= utilitySpace.getReservationValue())
					return new Accept(getPartyId(), lastOffer);
				else
					return new EndNegotiation(getPartyId());

		// MINIMUM_TARGETより高ければ受け入れる
		if(getUtility(lastOffer) >= MINIMUM_TARGET)
			return new Accept(getPartyId(), lastOffer);

		// Otherwise, send out a random offer above the target utility
		return new Offer(getPartyId(), generateBid());
	}

	private Bid generateBid()
	{
		Bid counterBid = generateRandomBid();
		Bid Bid;

		int i = 0;
		// try 100 times to find a bid under the target utility
		do
		{
			Bid = generateRandomBid();

			if(getUtility(Bid) >= getUtility(counterBid))
				counterBid = Bid;

		}
		while (i++ < 500);

		return counterBid;
	}

	/**
	 * Remembers the offers received by the opponent.
	 */
	@Override
	public void receiveMessage(AgentID sender, Action action)
	{
		if (action instanceof Offer)
		{
			lastOffer = ((Offer) action).getBid();
		}
	}

	@Override
	public String getDescription()
	{
		return "Places random bids >= " + MINIMUM_TARGET;
	}

	/**
	 * This stub can be expanded to deal with preference uncertainty in a more sophisticated way than the default behavior.
	 */
	@Override
	public AbstractUtilitySpace estimateUtilitySpace()
	{
		return super.estimateUtilitySpace();
	}

}
