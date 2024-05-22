import React, { useState } from "react";
import "../../styles/ProgressBarSummaries.css";
import { useTranslation } from "react-i18next";

interface GoalCardProps {
  name: string;
  goalAmount: number;
  amountSaved: number;
}
export default function ProgressBarSummaries(props: GoalCardProps) {
  const { t } = useTranslation();
  let [progress, setProgress] = useState(
    Math.floor((props.amountSaved / props.goalAmount) * 100)
  );
  const interval = 100;

  return (
    <>
      <section className="progress-container-s">
        <div style={{ display: "flex", justifyContent: "space-between" }}>
          <div>Amount Saved: <span className='text-mint'>${props.amountSaved}</span></div>
          <div style={{ marginLeft: "auto" }} className="text-right">
            Target: <span className='text-mint'>${props.goalAmount}</span>
          </div>
        </div>
        <progress className="progress-s" max="100" value={progress}></progress>
        <div className="progress-text">{progress}%</div>
      </section>
    </>
  );
}
