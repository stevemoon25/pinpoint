/*
 * Copyright 2020 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.grpc.client;

import com.navercorp.pinpoint.common.util.Assert;
import com.navercorp.pinpoint.grpc.Header;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;

import java.util.Set;

public class SupportCommandCodeClientInterceptor implements ClientInterceptor {

    private final Set<Short> supportCommandCodes;

    public SupportCommandCodeClientInterceptor(Set<Short> supportCommandCodes) {
        this.supportCommandCodes = Assert.requireNonNull(supportCommandCodes, "supportCommandCodes");
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        final ClientCall<ReqT, RespT> clientCall = next.newCall(method, callOptions);
        final ClientCall<ReqT, RespT> forwardingClientCall = new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(clientCall) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                for (Short code : supportCommandCodes) {
                    headers.put(Header.SUPPORT_COMMAND_CODE, String.valueOf(code));
                }
                super.start(responseListener, headers);
            }

        };
        return forwardingClientCall;
    }

}
