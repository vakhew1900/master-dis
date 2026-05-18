import type { GitComparisonResultDto, TwoGraphComparisonResultDto, MergedGraphComparisonResultDto } from '../api/generated/model';

export const isMergedResult = (result: GitComparisonResultDto): result is MergedGraphComparisonResultDto => {
  return 'merged_graph' in result;
};

export const isTwoGraphResult = (result: GitComparisonResultDto): result is TwoGraphComparisonResultDto => {
  return 'first_graph' in result;
};
