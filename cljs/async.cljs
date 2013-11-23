(ns bkblog.async
  (:require [enfocus.core :as ef]
            [enfocus.effects :as effects]
            [enfocus.events :as ev]
            [cljs.core.async :refer [put! chan <!]])
  (:require-macros [enfocus.macros :as em]
                   [cljs.core.async.macros :refer [go]]))

(defn aslisten [el type]
  (let [out (chan)]
    (ef/at el (ev/listen type
      #(let [target (.-currentTarget %)]
        (put! out target))))
    out))

(defn aslisten-live [el type]
  (let [out (chan)]
    (ef/at ".wrapper" (ev/listen-live type el
      #(let [target (.-currentTarget %)]
        (put! out target))))
    out))