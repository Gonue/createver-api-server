export default {
    namespaced: true,
    state: {
      originalImage: null,
    },
    mutations: {
      setOriginalImage(state, image) {
        state.originalImage = image;
      },
    },
    actions: {
      updateOriginalImage({ commit }, image) {
        commit('setOriginalImage', image);
      },
    },
  };