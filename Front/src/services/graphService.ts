import client from '../api/apiClient';

export const graphService = {
  compareGraphs: async (studentFile: File | null, referenceFile: File | null, method: 'TWO_GRAPH' | 'MERGED_GRAPH') => {
    // В будущем здесь будет реальный вызов:
    // const { data, error } = await client.POST("/api/comparison/compare-files", { ... });

    // Эмуляция задержки
    await new Promise(resolve => setTimeout(resolve, 800));

    // Подгружаем данные динамически через fetch, чтобы не перегружать Vite
    const mockPath = method === 'TWO_GRAPH' ? '/mock/two_graph.json' : '/mock/merged_graph.json';
    const response = await fetch(mockPath);
    
    if (!response.ok) {
        throw new Error(`Failed to load mock data: ${response.statusText}`);
    }

    const mockData = await response.json();

    if (method === 'TWO_GRAPH') {
      return {
        type: 'TwoGraphComparisonResultDto' as const,
        firstGraph: mockData.first_graph,
        secondGraph: mockData.second_graph,
        compareResult: {
          matchedHashes1To2: mockData.compare_result.matched_hashes_1_to_2
        }
      };
    } else {
      return {
        type: 'MergedGraphComparisonResultDto' as const,
        mergedGraph: mockData.merged_graph,
        compareResult: mockData.compare_result
      };
    }
  }
};
