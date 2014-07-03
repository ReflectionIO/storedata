//  
//  forum\ForumService.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.forum.client;

import io.reflection.app.api.forum.shared.call.AddReplyRequest;
import io.reflection.app.api.forum.shared.call.AddReplyResponse;
import io.reflection.app.api.forum.shared.call.CreateTopicRequest;
import io.reflection.app.api.forum.shared.call.CreateTopicResponse;
import io.reflection.app.api.forum.shared.call.DeleteReplyRequest;
import io.reflection.app.api.forum.shared.call.DeleteReplyResponse;
import io.reflection.app.api.forum.shared.call.DeleteTopicRequest;
import io.reflection.app.api.forum.shared.call.DeleteTopicResponse;
import io.reflection.app.api.forum.shared.call.GetForumsRequest;
import io.reflection.app.api.forum.shared.call.GetForumsResponse;
import io.reflection.app.api.forum.shared.call.GetRepliesRequest;
import io.reflection.app.api.forum.shared.call.GetRepliesResponse;
import io.reflection.app.api.forum.shared.call.GetReplyRequest;
import io.reflection.app.api.forum.shared.call.GetReplyResponse;
import io.reflection.app.api.forum.shared.call.GetTopicRequest;
import io.reflection.app.api.forum.shared.call.GetTopicResponse;
import io.reflection.app.api.forum.shared.call.GetTopicsRequest;
import io.reflection.app.api.forum.shared.call.GetTopicsResponse;
import io.reflection.app.api.forum.shared.call.UpdateReplyRequest;
import io.reflection.app.api.forum.shared.call.UpdateReplyResponse;
import io.reflection.app.api.forum.shared.call.UpdateTopicRequest;
import io.reflection.app.api.forum.shared.call.UpdateTopicResponse;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.client.HttpException;
import com.willshex.gson.json.service.client.JsonService;

public final class ForumService extends JsonService {
	public static final String ForumMethodGetForums = "GetForums";

	public Request getForums(final GetForumsRequest input, final AsyncCallback<GetForumsResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(ForumMethodGetForums, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						GetForumsResponse outputParameter = new GetForumsResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(ForumService.this, ForumMethodGetForums, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(ForumService.this, ForumMethodGetForums, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(ForumService.this, ForumMethodGetForums, input, exception);
				}
			});
			onCallStart(ForumService.this, ForumMethodGetForums, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(ForumService.this, ForumMethodGetForums, input, exception);
		}
		return handle;
	}

	public static final String ForumMethodGetTopics = "GetTopics";

	public Request getTopics(final GetTopicsRequest input, final AsyncCallback<GetTopicsResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(ForumMethodGetTopics, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						GetTopicsResponse outputParameter = new GetTopicsResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(ForumService.this, ForumMethodGetTopics, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(ForumService.this, ForumMethodGetTopics, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(ForumService.this, ForumMethodGetTopics, input, exception);
				}
			});
			onCallStart(ForumService.this, ForumMethodGetTopics, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(ForumService.this, ForumMethodGetTopics, input, exception);
		}
		return handle;
	}

	public static final String ForumMethodGetTopic = "GetTopic";

	public Request getTopic(final GetTopicRequest input, final AsyncCallback<GetTopicResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(ForumMethodGetTopic, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						GetTopicResponse outputParameter = new GetTopicResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(ForumService.this, ForumMethodGetTopic, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(ForumService.this, ForumMethodGetTopic, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(ForumService.this, ForumMethodGetTopic, input, exception);
				}
			});
			onCallStart(ForumService.this, ForumMethodGetTopic, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(ForumService.this, ForumMethodGetTopic, input, exception);
		}
		return handle;
	}

	public static final String ForumMethodGetReplies = "GetReplies";

	public Request getReplies(final GetRepliesRequest input, final AsyncCallback<GetRepliesResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(ForumMethodGetReplies, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						GetRepliesResponse outputParameter = new GetRepliesResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(ForumService.this, ForumMethodGetReplies, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(ForumService.this, ForumMethodGetReplies, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(ForumService.this, ForumMethodGetReplies, input, exception);
				}
			});
			onCallStart(ForumService.this, ForumMethodGetReplies, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(ForumService.this, ForumMethodGetReplies, input, exception);
		}
		return handle;
	}

	public static final String ForumMethodGetReply = "GetReply";

	public Request getReply(final GetReplyRequest input, final AsyncCallback<GetReplyResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(ForumMethodGetReply, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						GetReplyResponse outputParameter = new GetReplyResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(ForumService.this, ForumMethodGetReply, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(ForumService.this, ForumMethodGetReply, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(ForumService.this, ForumMethodGetReply, input, exception);
				}
			});
			onCallStart(ForumService.this, ForumMethodGetReply, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(ForumService.this, ForumMethodGetReply, input, exception);
		}
		return handle;
	}

	public static final String ForumMethodCreateTopic = "CreateTopic";

	public Request createTopic(final CreateTopicRequest input, final AsyncCallback<CreateTopicResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(ForumMethodCreateTopic, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						CreateTopicResponse outputParameter = new CreateTopicResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(ForumService.this, ForumMethodCreateTopic, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(ForumService.this, ForumMethodCreateTopic, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(ForumService.this, ForumMethodCreateTopic, input, exception);
				}
			});
			onCallStart(ForumService.this, ForumMethodCreateTopic, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(ForumService.this, ForumMethodCreateTopic, input, exception);
		}
		return handle;
	}

	public static final String ForumMethodAddReply = "AddReply";

	public Request addReply(final AddReplyRequest input, final AsyncCallback<AddReplyResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(ForumMethodAddReply, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						AddReplyResponse outputParameter = new AddReplyResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(ForumService.this, ForumMethodAddReply, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(ForumService.this, ForumMethodAddReply, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(ForumService.this, ForumMethodAddReply, input, exception);
				}
			});
			onCallStart(ForumService.this, ForumMethodAddReply, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(ForumService.this, ForumMethodAddReply, input, exception);
		}
		return handle;
	}

	public static final String ForumMethodUpdateTopic = "UpdateTopic";

	public Request updateTopic(final UpdateTopicRequest input, final AsyncCallback<UpdateTopicResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(ForumMethodUpdateTopic, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						UpdateTopicResponse outputParameter = new UpdateTopicResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(ForumService.this, ForumMethodUpdateTopic, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(ForumService.this, ForumMethodUpdateTopic, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(ForumService.this, ForumMethodUpdateTopic, input, exception);
				}
			});
			onCallStart(ForumService.this, ForumMethodUpdateTopic, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(ForumService.this, ForumMethodUpdateTopic, input, exception);
		}
		return handle;
	}

	public static final String ForumMethodUpdateReply = "UpdateReply";

	public Request updateReply(final UpdateReplyRequest input, final AsyncCallback<UpdateReplyResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(ForumMethodUpdateReply, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						UpdateReplyResponse outputParameter = new UpdateReplyResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(ForumService.this, ForumMethodUpdateReply, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(ForumService.this, ForumMethodUpdateReply, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(ForumService.this, ForumMethodUpdateReply, input, exception);
				}
			});
			onCallStart(ForumService.this, ForumMethodUpdateReply, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(ForumService.this, ForumMethodUpdateReply, input, exception);
		}
		return handle;
	}

	public static final String ForumMethodDeleteTopic = "DeleteTopic";

	public Request deleteTopic(final DeleteTopicRequest input, final AsyncCallback<DeleteTopicResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(ForumMethodDeleteTopic, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						DeleteTopicResponse outputParameter = new DeleteTopicResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(ForumService.this, ForumMethodDeleteTopic, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(ForumService.this, ForumMethodDeleteTopic, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(ForumService.this, ForumMethodDeleteTopic, input, exception);
				}
			});
			onCallStart(ForumService.this, ForumMethodDeleteTopic, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(ForumService.this, ForumMethodDeleteTopic, input, exception);
		}
		return handle;
	}

	public static final String ForumMethodDeleteReply = "DeleteReply";

	public Request deleteReply(final DeleteReplyRequest input, final AsyncCallback<DeleteReplyResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(ForumMethodDeleteReply, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						DeleteReplyResponse outputParameter = new DeleteReplyResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(ForumService.this, ForumMethodDeleteReply, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(ForumService.this, ForumMethodDeleteReply, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(ForumService.this, ForumMethodDeleteReply, input, exception);
				}
			});
			onCallStart(ForumService.this, ForumMethodDeleteReply, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(ForumService.this, ForumMethodDeleteReply, input, exception);
		}
		return handle;
	}
}