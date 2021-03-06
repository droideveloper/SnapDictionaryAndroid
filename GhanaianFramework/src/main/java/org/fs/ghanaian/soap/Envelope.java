/*
Copyright 2014 GhanaianFramework.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.fs.ghanaian.soap;

import org.fs.ghanaian.annotation.Accessibility;
import org.fs.ghanaian.annotation.Element;
import org.fs.ghanaian.annotation.Ignore;
import org.fs.ghanaian.annotation.Namespace;

/**
 * Created by Fatih on 19/10/14.
 */
@Element(name = "Envelope")
@Namespace(prefix = "soapenv", referance = "http://schemas.xmlsoap.org/soap/envelope/")
public class Envelope<H, B> {

    @Accessibility(get = "header", set = "header")
    public Header<H> header = null;

    @Accessibility(get = "body", set = "body")
    public Body<B> body     = null;

    @Ignore
    public Envelope header(Header<H> header) {
        this.header = header;
        return this;
    }

    @Ignore
    public Envelope body(Body<B> body) {
        this.body = body;
        return this;
    }

    @Ignore
    public Header<H> header() {
        return this.header;
    }

    @Ignore
    public Body<B> body() {
        return this.body;
    }

}
