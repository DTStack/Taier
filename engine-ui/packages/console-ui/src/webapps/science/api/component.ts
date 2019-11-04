import http from './http'
import req from '../consts/reqUrls'

export default {
    getClusterRegressionGraph (params: {
        taskId: number;
    }) {
        return http.post(req.GET_CLUSTER_REGRESSTION_GRAPH, params);
    },
    getRegressionEvaluationGraph (params: {
        taskId: number;
    }) {
        return http.post(req.GET_REGRESSION_EVALUATION_GRAPH, params);
    },
    getMissValueGraph (params: {
        taskId: number;
    }) {
        return http.post(req.GET_MISSVALUE_GRAPH, params)
    }
}
