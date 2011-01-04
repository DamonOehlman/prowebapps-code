package com.jsonengine.controller;

import org.slim3.controller.router.RouterImpl;


public class AppRouter extends RouterImpl {

    public AppRouter() {

        // CRUDController
        addRouting("/_je/{_docType}", "/front?_docType={_docType}");
        addRouting("/_je/{_docType}/{_docId}", "/front?_docType={_docType}&_docId={_docId}");

        // QueryController
//        addRouting("/_q/{doctype}", "/query?docType={doctype}");
    }

}
