(ns {{name}}.content-script.common
    (:require-macros [cljs.core.async.macros :refer [go]])
    (:require [cljs.core.async :refer [<!]]
              [cognitect.transit :as t]
              [chromex.ext.windows :as windows]
              [chromex.ext.tabs :as tabs]
              ))

(defn marshall [edn-msg]
  (let [w (t/writer :json)]
    (t/write w edn-msg)))

(defn unmarshall [msg-str]
  (let [r (t/reader :json)]
    (t/read r msg-str)))

(defn get-active-tab-id []
  (go
    (let [w (<! (windows/get-current))
          curr-window-id (-> w js->clj first (get "id"))
          all-active-tabs (<! (tabs/query #js{:active true}))
          t (->> all-active-tabs
                 js->clj
                 first
                 (filter (fn [x]
                           (= (get x "windowId") curr-window-id)
                           ))
                 first)]
      (-> t (get "id"))
      )))
