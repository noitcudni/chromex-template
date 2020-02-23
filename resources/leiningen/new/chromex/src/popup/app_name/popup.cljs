(ns {{name}}.popup
  (:require-macros [chromex.support :refer [runonce]])
  (:require [{{name}}.popup.core :as core]))

(runonce
  (core/init!))
