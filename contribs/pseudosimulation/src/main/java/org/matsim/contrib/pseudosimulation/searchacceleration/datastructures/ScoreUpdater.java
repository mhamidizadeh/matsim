/*
 * Copyright 2018 Gunnar Flötteröd
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * contact: gunnar.flotterod@gmail.com
 *
 */
package org.matsim.contrib.pseudosimulation.searchacceleration.datastructures;

import java.util.Map;

import org.matsim.contrib.pseudosimulation.searchacceleration.ReplanningParameterContainer;
import org.matsim.core.router.util.TravelTime;

import floetteroed.utilities.DynamicData;
import floetteroed.utilities.Tuple;

/**
 * The "score" this class refers to is the anticipated change of the search
 * acceleration objective function resulting from setting a single agent's
 * (possibly space-weighted) 0/1 re-planning indicator.
 * 
 * Implements the score used in the greedy heuristic of Merz, P. and Freisleben,
 * B. (2002). "Greedy and local search heuristics for unconstrained binary
 * quadratic programming." Journal of Heuristics 8:197–213.
 * 
 * @author Gunnar Flötteröd
 * 
 * @param L
 *            the space coordinate type
 *
 */
public class ScoreUpdater<L> {

	// -------------------- MEMBERS --------------------

	private final SpaceTimeCounts<L> currentIndividualWeightedCounts;

	private final SpaceTimeCounts<L> individualWeightedChanges;

	private final DynamicData<L> currentWeightedTotalCounts;

	private final DynamicData<L> interactionResiduals;

	private final double deltaScoreIndividual;

	// private final DynamicData<L> inertiaResiduals;

	private double regularizationResidual;

	private final double scoreChangeIfZero;

	private final double scoreChangeIfOne;

	private boolean residualsUpdated = false;

	// -------------------- CONSTRUCTION --------------------

	public ScoreUpdater(final SpaceTimeIndicators<L> currentIndicators, final SpaceTimeIndicators<L> upcomingIndicators,
			final double meanLambda, final DynamicData<L> currentWeightedTotalCounts, final double w,
			final double delta, final DynamicData<L> interactionResiduals,
			// final DynamicData<L> inertiaResiduals,
			final double regularizationResidual, final ReplanningParameterContainer replParams,
			final TravelTime travelTimes,
			// final double numberOfRelevantLinkTimeSlots,
			final double deltaScoreIndividual, final double deltaScoreTotal) {

		this.currentWeightedTotalCounts = currentWeightedTotalCounts;
		this.interactionResiduals = interactionResiduals;
		// this.inertiaResiduals = inertiaResiduals;
		this.regularizationResidual = regularizationResidual;
		this.deltaScoreIndividual = deltaScoreIndividual;

		/*
		 * One has to go beyond 0/1 indicator arithmetics in the following because the
		 * same vehicle may enter the same link multiple times during one time bin.
		 */

		this.currentIndividualWeightedCounts = new SpaceTimeCounts<>(currentIndicators, replParams, travelTimes);
		this.individualWeightedChanges = new SpaceTimeCounts<>(upcomingIndicators, replParams, travelTimes);
		this.individualWeightedChanges.subtract(this.currentIndividualWeightedCounts);

		// Update the residuals.

		for (Map.Entry<Tuple<L, Integer>, Double> entry : this.individualWeightedChanges.entriesView()) {
			final L spaceObj = entry.getKey().getA();
			final int timeBin = entry.getKey().getB();
			final double weightedIndividualChange = entry.getValue();
			this.interactionResiduals.add(spaceObj, timeBin, -meanLambda * weightedIndividualChange);
		}

		this.regularizationResidual -= meanLambda * deltaScoreIndividual;
		// for (Map.Entry<Tuple<L, Integer>, Double> entry :
		// this.currentIndividualWeightedCounts.entriesView()) {
		// final L spaceObj = entry.getKey().getA();
		// final Integer timeBin = entry.getKey().getB();
		// final double weightedCurrentIndividualCount = entry.getValue();
		// // this.inertiaResiduals.add(spaceObj, timeBin, -(1.0 - meanLambda) *
		// // weightedCurrentIndividualCount);
		// // this.regularizationResiduals.add(spaceObj, timeBin, -meanLambda *
		// weightedCurrentIndividualCount);
		// }

		// Compute individual score terms.

		double sumOfWeightedIndividualChanges2 = 0.0;
		double sumOfWeightedIndividualChangesTimesInteractionResiduals = 0.0;

		for (Map.Entry<Tuple<L, Integer>, Double> entry : this.individualWeightedChanges.entriesView()) {
			final L spaceObj = entry.getKey().getA();
			final int timeBin = entry.getKey().getB();
			final double weightedIndividualChange = entry.getValue();
			sumOfWeightedIndividualChanges2 += weightedIndividualChange * weightedIndividualChange;
			sumOfWeightedIndividualChangesTimesInteractionResiduals += weightedIndividualChange
					* this.interactionResiduals.getBinValue(spaceObj, timeBin);
		}

		// double sumOfLinkShares2 = 0;
		// double
		// sumOfWeightedCurrentIndividualCountsTimesInertiaResidualsOverWeightedSquareCounts
		// = 0;
		// double
		// sumOfWeightedCurrentIndividualCountsTimesRegularizationResidualsOverWeigthedSquareCounts
		// = 0;
		// double sumOfWeightedCurrentIndividualCounts2 = 0.0;
		// double sumOfWeightedCurrentIndividualCountsTimesWeightedCurrentTotalCounts =
		// 0.0;
		// double sumOfWeightedCurrentIndividualCountsTimesInertiaResiduals = 0.0;

		for (Map.Entry<Tuple<L, Integer>, Double> entry : this.currentIndividualWeightedCounts.entriesView()) {
			final L spaceObj = entry.getKey().getA();
			final Integer timeBin = entry.getKey().getB();
			final double weightedCurrentIndividualCount = entry.getValue();
			// sumOfWeightedCurrentIndividualCounts2 += weightedCurrentIndividualCount *
			// weightedCurrentIndividualCount;
			// sumOfWeightedCurrentIndividualCountsTimesWeightedCurrentTotalCounts +=
			// weightedCurrentIndividualCount
			// * this.currentWeightedTotalCounts.getBinValue(spaceObj, timeBin);
			// sumOfWeightedCurrentIndividualCountsTimesInertiaResiduals +=
			// weightedCurrentIndividualCount
			// * this.inertiaResiduals.getBinValue(spaceObj, timeBin);
			final double weightedCurrentTotalCount = this.currentWeightedTotalCounts.getBinValue(spaceObj, timeBin);
			if (weightedCurrentTotalCount > 1e-6) {
				// sumOfLinkShares2 += Math.pow(weightedCurrentIndividualCount /
				// weightedCurrentTotalCount, 2.0);
				// sumOfWeightedCurrentIndividualCountsTimesInertiaResidualsOverWeightedSquareCounts
				// += weightedCurrentIndividualCount
				// * this.inertiaResiduals.getBinValue(spaceObj, timeBin) /
				// weightedCurrentTotalCount
				// / weightedCurrentTotalCount;
				// sumOfWeightedCurrentIndividualCountsTimesRegularizationResidualsOverWeigthedSquareCounts
				// += weightedCurrentIndividualCount
				// * this.regularizationResiduals.getBinValue(spaceObj, timeBin) /
				// weightedCurrentTotalCount
				// / weightedCurrentTotalCount;
			}
		}

		// Compose the actual score change.

		this.factor1 = sumOfWeightedIndividualChanges2 + delta * deltaScoreIndividual * deltaScoreIndividual;
		this.factor2 = 2.0 * sumOfWeightedIndividualChangesTimesInteractionResiduals - w * deltaScoreIndividual
				+ 2 * delta * deltaScoreIndividual * regularizationResidual;
		this.meanLambda = meanLambda;

		this.scoreChangeIfOne = (1.0 - meanLambda * meanLambda) * this.factor1 + (1.0 - meanLambda) * this.factor2;
		this.scoreChangeIfZero = (0.0 - meanLambda * meanLambda) * this.factor1 + (0.0 - meanLambda) * this.factor2;
	}

	private final double factor1;
	private final double factor2;
	private final double meanLambda;

	public double getScoreChange(final double newLambda) {
		return (newLambda * newLambda - this.meanLambda * this.meanLambda) * this.factor1
				+ (newLambda - this.meanLambda) * this.factor2;
	}

	// -------------------- GETTERS --------------------

	public double getUpdatedRegularizationResidual() {
		if (!this.residualsUpdated) {
			throw new RuntimeException("Residuals have not yet updated.");
		}
		return this.regularizationResidual;
	}

	public double getScoreChangeIfOne() {
		return this.scoreChangeIfOne;
	}

	public double getScoreChangeIfZero() {
		return this.scoreChangeIfZero;
	}

	// -------------------- IMPLEMENTATION --------------------

	public void updateResiduals(final double newLambda) {
		if (this.residualsUpdated) {
			throw new RuntimeException("Residuals have already been updated.");
		}
		this.residualsUpdated = true;

		for (Map.Entry<Tuple<L, Integer>, Double> entry : this.individualWeightedChanges.entriesView()) {
			final L spaceObj = entry.getKey().getA();
			final int timeBin = entry.getKey().getB();
			this.interactionResiduals.add(spaceObj, timeBin, newLambda * entry.getValue());
		}

		// for (Map.Entry<Tuple<L, Integer>, Double> entry :
		// this.currentIndividualWeightedCounts.entriesView()) {
		// final L spaceObj = entry.getKey().getA();
		// final int timeBin = entry.getKey().getB();
		// // this.inertiaResiduals.add(spaceObj, timeBin, (1.0 - newLambda) *
		// // entry.getValue());
		// this.regularizationResiduals.add(spaceObj, timeBin, newLambda *
		// entry.getValue());
		// }
		this.regularizationResidual += newLambda * this.deltaScoreIndividual;
	}
}
