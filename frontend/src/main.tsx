import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App.tsx";
import { Provider } from "react-redux";
import { persistor, store } from "./app/store.tsx";
import { PersistGate } from "redux-persist/integration/react";
import "@trussworks/react-uswds/lib/index.css";
import "./i18n.tsx";

ReactDOM.createRoot(document.getElementById("root")!).render(
  //<React.StrictMode>
    <Provider store={store}>
      <PersistGate loading={null} persistor={persistor}>
        <App/>
      </PersistGate>
    </Provider>
  //</React.StrictMode>
);
