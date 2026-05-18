module.exports = {
  'jserver-api': {
    input: './open-api.json',
    output: {
      target: './src/api/generated/jserver.ts',
      schemas: './src/api/generated/model',
      mode: 'single', // 'single' часто лучше справляется с циклическими ссылками в типах
      client: 'axios',
      override: {
        mutator: {
          path: './src/api/apiClient.ts',
          name: 'customInstance',
        },
      },
    },
  },
};
