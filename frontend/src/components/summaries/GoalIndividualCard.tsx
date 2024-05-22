import {
  Button,
  Card,
  CardBody,
  CardFooter,
  CardHeader,
} from "@trussworks/react-uswds";
import ProgressBarSummaries from "./ProgressBarSummaries";
import { useEffect, useState } from "react";

interface GoalCardProps {
  name: string;
  goalAmount: number;
  amountSaved: number;
  isExpanded: boolean;
}

export default function GoalIndividualCard(props: GoalCardProps) {
  const [localIsExpanded, setLocalIsExpanded] = useState(props.isExpanded);
  useEffect(() => {
    setLocalIsExpanded(props.isExpanded);
  }, [props.isExpanded]);

  const toggleExpand = () => {
    setLocalIsExpanded(!localIsExpanded);
  };
  return (
    <>
      <Card
        layout="flagMediaRight"
        gridLayout={{ tablet: { col: 12 } }}
        className="goalSummary"
        onClick={toggleExpand}
      >
        {localIsExpanded ? (
          <>
            <CardHeader>
              <h3 className="usa-card__heading">{props.name}</h3>
            </CardHeader>
            <CardBody style={{ width: "100%" }}>
              <ProgressBarSummaries
                name={props.name}
                goalAmount={props.goalAmount}
                amountSaved={props.amountSaved}
              />
            </CardBody>
          </>
        ) : (
          <>
            <div
              style={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
              }}
            >
              <h3 className="usa-card__heading">{props.name}</h3>
              <div>
                {props.amountSaved / props.goalAmount != 1 && (
                  <h5 className="usa-card__heading">
                    ${props.amountSaved}/
                    <span className="text-mint">${props.goalAmount}</span>
                  </h5>
                )}
                {props.amountSaved / props.goalAmount == 1 && (
                  <h5 className="usa-card__heading text-mint">
                    <span className="text-mint">Completed</span>
                  </h5>
                )}
              </div>
            </div>
          </>
        )}
      </Card>
    </>
  );
}
