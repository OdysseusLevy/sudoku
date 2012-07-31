(ns org.cosmosgame.sudoku.workers
  (:use org.cosmosgame.sudoku.core clojure.tools.logging))

(defn queue [] (java.util.concurrent.LinkedBlockingQueue.))

(defn offer
  "adds x to the back of queue q. Ignores nil inputs"
  [q x]
  (debug "adding " x)
  (if (not (nil? x))
    (.offer q x)))

(defn offer-all
  "adds every item of a sequence to q"
  [q s]
  (offer q (first s))
  (if (not (empty? (rest s)))
    (recur q (rest s))))

(defn consumer
  [q pred result]
  (future
    ;(println "consumer launched")
    (while (not (realized? result))
      (debug "polling: " (.size q))
      ;; Use .poll instead of .take so that we don't just hang when done
      ;; Note that this means we can't distinguish between a nil put on the queue vs. when we time out on the poll
      (when-let [item (.poll q 500 (java.util.concurrent.TimeUnit/MILLISECONDS))]
        (debug "processing: " item)
        (pred item result)))
    (debug "finished with consumer")
    ))

(defn start-workers
  ([q pred result]
    (let [n (+ 2 (.. Runtime getRuntime availableProcessors))]
      (start-workers n q pred result)))
  ([num q pred result]
    (doall (take num (repeatedly (partial consumer q pred result) )))))

(defn psolve-helper
  [q board result]

  (if (nil? board)
    nil
    (if (solved? board)
      (deliver result (:answers board))
      (if (realized? result)
        (dorun (debug "realized") result)
        (let [moves (next-moves board)
              _ (debug "moves: " moves)
              boards (map #(add-move board %) moves)
              _ (debug "boards: " boards)]
          (psolve-helper q (first boards) result)
          (if (not (realized? result))
            (offer-all q (rest boards)
                                      )))))))

(defn psolve
  [board]
  (let [q (queue)
        result (promise)
        workers (start-workers q (partial psolve-helper q) result)]
    (offer q board)
    (deref result)
    (info "result:" result)
    result))

(defn psolve-read
  "Sudoku solver"
  [board-str]
  (let [board (read-board board-str) ]
    (time (psolve board))))

