package simulation;

import modules.Link;
import agents.Business;
import agents.Customer;
import agents.MaterialSource;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

public class SimBuilder implements ContextBuilder<Object> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * repast.simphony.dataLoader.ContextBuilder#build(repast.simphony.context
	 * .Context)
	 */
	@Override
	public Context build(Context<Object> context) {
		context.setId("context");

		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>(
				"infection network", context, true);
		netBuilder.buildNetwork();

		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder
				.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace(
				"space", context, new RandomCartesianAdder<Object>(),
				new repast.simphony.space.continuous.WrapAroundBorders(), 50,
				50);

		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Object>(new WrapAroundBorders(),
						new SimpleGridAdder<Object>(), true, 50, 50));

		Parameters params = RunEnvironment.getInstance().getParameters();
		
		Setup setup = new Setup();
		setup.retailerSetUp();
		setup.print();
		
		for(MaterialSource source : setup.getMaterialSources()){
			context.add(source);
		}
		
		for(Business biz : setup.getBusinesses()) {
			context.add(biz);
		}
		
		for(Customer customer : setup.getCustomers()){
			context.add(customer);
		}

		for (Link link : setup.getLinks()) {
			context.add(link);
		}
		
		context.add(setup);
		
		if (RunEnvironment.getInstance().isBatch()) {
			RunEnvironment.getInstance().endAt(20);
		}

		return context;
	}
}
