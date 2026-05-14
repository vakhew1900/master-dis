import api from '../api/apiClient';

export const graphService = {
  compareGraphs: async (repo1: string, repo2: string, method: 'two_graph' | 'merged') => {
    // Mock response for now
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          data: {
            id: 'mock-report-id',
            repo1,
            repo2,
            method,
            graphData: { nodes: [], edges: [] } // Placeholder structure
          }
        });
      }, 1000);
    });
  }
};
