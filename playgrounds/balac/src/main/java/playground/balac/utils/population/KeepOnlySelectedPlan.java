package playground.balac.utils.population;

import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.MatsimNetworkReader;
import org.matsim.core.population.MatsimPopulationReader;
import org.matsim.core.population.PersonImpl;
import org.matsim.core.population.PopulationReader;
import org.matsim.core.population.PopulationWriter;
import org.matsim.core.scenario.ScenarioImpl;
import org.matsim.core.scenario.ScenarioUtils;

public class KeepOnlySelectedPlan {

	public static void main(String[] args) {
		ScenarioImpl scenario = (ScenarioImpl) ScenarioUtils.createScenario(ConfigUtils.createConfig());
		PopulationReader populationReader = new MatsimPopulationReader(scenario);
		MatsimNetworkReader networkReader = new MatsimNetworkReader(scenario);
		networkReader.readFile(args[0]);
	//	new FacilitiesReaderMatsimV1(scenario).readFile(facilitiesfilePath);
		populationReader.readFile(args[1]);
		ScenarioImpl scenario2 = (ScenarioImpl) ScenarioUtils.createScenario(ConfigUtils.createConfig());

		for (Person p : scenario.getPopulation().getPersons().values()) {
			
			Person p1 = scenario2.getPopulation().getFactory().createPerson(p.getId());
			p1.addPlan(p.getSelectedPlan());
			p1.setSelectedPlan(p.getSelectedPlan());
			
			PersonImpl.setAge(p1, PersonImpl.getAge(p));
			PersonImpl.setCarAvail(p1, PersonImpl.getCarAvail(p));
			PersonImpl.setSex(p1, PersonImpl.getSex(p));
			PersonImpl.setLicence(p1, PersonImpl.getLicense(p));
			scenario2.getPopulation().addPerson(p1);
		}
		
		
		
		new PopulationWriter(scenario2.getPopulation(), scenario.getNetwork()).writeFileV4(args[2] + "/plans_normal.xml.gz");		

	}

}
