import { getOpenAPIDefinition } from '../api/generated/jserver';
import type { GitComparisonResultDto } from '../api/generated/model';

const api = getOpenAPIDefinition();

export type GitComparisonResult = GitComparisonResultDto;

export const graphService = {
  compareFiles: async (
    reference: File, 
    student: File, 
    params: { reportType?: "TWO_GRAPH" | "MERGED_GRAPH"; method?: "BRANCH" | "BRUTE_FORCE" | "DP" | "UNIQUE_LABEL" } = {}
  ): Promise<GitComparisonResult> => {
    return await api.compareFiles({ reference, student }, params);
  },

  checkSolution: async (
    taskId: number, 
    params: { reportType?: "TWO_GRAPH" | "MERGED_GRAPH"; method?: "BRANCH" | "BRUTE_FORCE" | "DP" | "UNIQUE_LABEL" } = {}
  ): Promise<GitComparisonResult> => {
    return await api.checkSolution(taskId, params);
  },

  checkSubmission: async (
    submissionId: number, 
    params: { reportType?: "TWO_GRAPH" | "MERGED_GRAPH"; method?: "BRANCH" | "BRUTE_FORCE" | "DP" | "UNIQUE_LABEL" } = {}
  ): Promise<GitComparisonResult> => {
    return await api.checkSubmission(submissionId, params);
  },

  checkRepositoryByTaskId: async (
    taskId: number,
    file: File,
    params: { reportType?: "TWO_GRAPH" | "MERGED_GRAPH"; method?: "BRANCH" | "BRUTE_FORCE" | "DP" | "UNIQUE_LABEL" } = {}
  ): Promise<GitComparisonResult> => {
    return await api.checkRepositoryByTaskId(taskId, { file }, params);
  }
};
