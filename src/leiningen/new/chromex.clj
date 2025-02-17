(ns leiningen.new.chromex
  (:require [leiningen.new.templates :refer [renderer name-to-path ->files]]
            [leiningen.core.main :as main]))

(def render (renderer "chromex"))

(defn chromex
  "FIXME: write documentation"
  [name]
  (let [data {:name name
              :sanitized (name-to-path name)}]
    (main/info "Generating fresh 'lein new' chromex project.")
    (->files data
             ["project.clj" (render "project.clj" data)]
             ["resources/release/images/icon16.png" (render "resources/release/images/icon16.png" data)]
             ["resources/release/images/icon38.png" (render "resources/release/images/icon38.png" data)]
             ["resources/release/images/icon48.png" (render "resources/release/images/icon48.png" data)]
             ["resources/release/images/icon128.png" (render "resources/release/images/icon128.png" data)]
             ["resources/release/images/icon19.png" (render "resources/release/images/icon19.png" data)]
             ["resources/release/popup.html" (render "resources/release/popup.html" data)]
             ["resources/release/manifest.json" (render "resources/release/manifest.json" data)]
             ["resources/release/background.html" (render "resources/release/background.html" data)]
             ["resources/shared/images/icon16.png" (render "resources/shared/images/icon16.png" data)]
             ["resources/shared/images/icon38.png" (render "resources/shared/images/icon38.png" data)]
             ["resources/shared/images/icon48.png" (render "resources/shared/images/icon48.png" data)]
             ["resources/shared/images/icon128.png" (render "resources/shared/images/icon128.png" data)]
             ["resources/shared/images/icon19.png" (render "resources/shared/images/icon19.png" data)]

             ["resources/unpacked/popup.js" (render "resources/unpacked/popup.js" data)]
             ["resources/unpacked/background.js" (render "resources/unpacked/background.js" data)]
             ["resources/unpacked/images/icon16.png" (render "resources/unpacked/images/icon16.png" data)]
             ["resources/unpacked/images/icon38.png" (render "resources/unpacked/images/icon38.png" data)]
             ["resources/unpacked/images/icon48.png" (render "resources/unpacked/images/icon48.png" data)]
             ["resources/unpacked/images/icon128.png" (render "resources/unpacked/images/icon128.png" data)]
             ["resources/unpacked/images/icon19.png" (render "resources/unpacked/images/icon19.png" data)]
             ["resources/unpacked/popup.html" (render "resources/unpacked/popup.html" data)]
             ["resources/unpacked/manifest.json" (render "resources/unpacked/manifest.json" data)]
             ["resources/unpacked/setup.js" (render "resources/unpacked/setup.js" data)]
             ["resources/unpacked/background.html" (render "resources/unpacked/background.html" data)]
             ["readme.md" (render "readme.md" data)]

             ["scripts/launch-test-browser.sh" (render "scripts/launch-test-browser.sh" data)]
             ["scripts/_config.sh" (render "scripts/_config.sh" data)]
             ["scripts/package.sh" (render "scripts/package.sh" data)]
             ["src/background/{{sanitized}}/background/storage.cljs" (render "src/background/app_name/background/storage.cljs" data)]
             ["src/background/{{sanitized}}/background/core.cljs" (render "src/background/app_name/background/core.cljs" data)]
             ["src/background/{{sanitized}}/background.cljs" (render "src/background/app_name/background.cljs" data)]
             ["src/popup/{{sanitized}}/popup/core.cljs" (render "src/popup/app_name/popup/core.cljs" data)]
             ["src/popup/{{sanitized}}/popup.cljs" (render "src/popup/app_name/popup.cljs" data)]
             ["src/content_script/{{sanitized}}/content_script/core.cljs" (render "src/content_script/app_name/content_script/core.cljs" data)]
             ["src/content_script/{{sanitized}}/content_script/common.cljs" (render "src/content_script/app_name/content_script/common.cljs" data)]
             ["src/content_script/{{sanitized}}/content_script.cljs" (render "src/content_script/app_name/content_script.cljs" data)]
             )))
