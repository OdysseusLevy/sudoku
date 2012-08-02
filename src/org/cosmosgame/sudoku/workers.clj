(ns org.cosmosgame.sudoku.workers
  (:use org.cosmosgame.sudoku.core clojure.tools.logging))

(defn queue 
  ([]
    (queue (.. Runtime getRuntime availableProcessors)))
  ([num-workers]
    (let [q {}
        q (assoc q :lbq (java.util.concurrent.LinkedBlockingQueue.) )
        q (assoc q :num-workers num-workers )
        q (assoc q :result (promise))
        q (assoc q :idle-workers (atom (:num-workers q)))]
    q)))

(defn offer
  "Adds x to the back of queue q. Ignores nil inputs"
  [q x]
  (debug "adding " x)
  (if (not (nil? x))
    (.offer (:lbq q) x)))

(defn offer-all
  "Adds every item of a sequence to q"
  [q s]
  (offer q (first s))
  (if (not (empty? (rest s)))
    (recur q (rest s))))

(defn check-stop
  "Check to see if all of our workers are waiting for input. If we need to manually stop"
  [q]

  (debug "idle workers: " @(:idle-workers q))
  (if (not (realized? (:result q)))
    (if (and (= @(:idle-workers q) (:num-workers q)))
      (do
        (info "stopping stalled process")
        (deliver (:result q) nil)
        ; force any other workers to quit
        (doall (repeatedly (:num-workers q) (partial (offer q nil))) )))))

(defn do-job
  [q pred item]
  (debug "do-job: " item)

  (swap! (:idle-workers q) - 1)
  (pred q item)
  (swap! (:idle-workers q) + 1) )

(defn consumer
  [q pred]
  (future
    (debug "consumer launched")
    (while (not (realized? (:result q)))
      (let [item (.poll (:lbq q) 500 (java.util.concurrent.TimeUnit/MILLISECONDS))]
        (debug "took item: " item)
        (if (nil? item)
          (check-stop q)
          (do-job q pred item))))
    (debug "finished with consumer")
    (:result q)))

(defn start-workers
  [q pred]
    (doall (repeatedly (:num-workers q) (partial consumer q pred) )))

(defn psolve-helper
  [q board]

  (debug "psolve-helper: " board)
  (if (realized? (:result q))
    @(:result q)
    (if (nil? board)
      nil
      (if (solved? board)
        (do (info "answer found!: " (:answers board))
            (deliver (:result q) (:answers board)))

        (let [moves (next-moves board)
                _ (debug "moves: " moves)
                boards (map #(add-move board %) moves)]
              (psolve-helper q (first boards))
              (offer-all q (rest boards)))))))

(defn psolve
  [board]
  (let [q (queue)
        workers (start-workers q psolve-helper )]
    (offer q board)
    @(:result q)))

(defn psolve-main
  "Sudoku solver"
  [board-str]
  (let [board (read-board board-str)
        answer (time (psolve board))]
    (shutdown-agents) ; if we don't do this the jvm never exits
    (println answer)
    answer))

