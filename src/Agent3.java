import genius.core.AgentID;
import genius.core.Bid;
import genius.core.actions.Accept;
import genius.core.actions.Action;
import genius.core.parties.SessionsInfo;
import genius.core.persistent.PersistentDataType;
import genius.core.session.*;
import genius.core.actions.EndNegotiation;
import genius.core.actions.Offer;
import genius.core.bidding.BidDetails;
import genius.core.boaframework.SortedOutcomeSpace;
import genius.core.misc.Range;
import genius.core.parties.AbstractNegotiationParty;
import genius.core.parties.NegotiationInfo;
import genius.core.utility.AbstractUtilitySpace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;


/**
 * A simple example agent that makes random bids above a minimum target utility.
 *
 * @author Takumu Shimizu
 */
public class Agent3 extends AbstractNegotiationParty
{
	private static double MINIMUM_TARGET = 0.85;
	private Bid lastOffer;
	private int round_num = 0;
	private NegotiationInfo info_copy;


	/**
	 * Initializes a new instance of the agent.
	 */
	@Override
	public void init(NegotiationInfo info){
		super.init(info);
		info_copy = info;
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

	private int i = 0;

	private Bid generateBid() {
		SortedOutcomeSpace sortedOutcomeSpace = new SortedOutcomeSpace(getUtilitySpace());
		
		//可能なビッドのリスト
		List<BidDetails> list= new ArrayList<>();
		list = sortedOutcomeSpace.getBidsinRange(new Range(0,1));
		int list_num = list.size();

		round_num = info_copy.getDeadline().getValue();		//ラウンド数の取得


		return list.get((int)(list_num*timeline.getTime())).getBid();
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
		return "Offer in ascending order";
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
