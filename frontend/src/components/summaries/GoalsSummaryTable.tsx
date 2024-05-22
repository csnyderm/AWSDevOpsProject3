import {
  Button,
  Card,
  CardBody,
  CardHeader,
  Icon,
} from "@trussworks/react-uswds";
import { useState } from "react";
import GoalIndividualCard from "./GoalIndividualCard";
import React from "react";
import { useGetGoalsByEmailQuery } from "../../app/api/goalsApi";
import { useSelector } from "react-redux";
import { selectUserEmail } from "../../app/features/userSlice";
import { Link, Navigate } from "react-router-dom";
import "../../styles/goalCard.css";
import { useTranslation } from "react-i18next";

export default function GoalsSummaryTable() {
  const userEmail = useSelector(selectUserEmail);
  const [isExpand, setIsExpand] = useState(false);

  const { data, error, isLoading } = useGetGoalsByEmailQuery(userEmail);
  const { t } = useTranslation();

  if (!isLoading) {
    if (data != null && data.length > 0) {
      return (
        <Card
          headerFirst
          //gridLayout={{ tablet: { col: 6 }, desktop: { col: "fill" } }}
          className="goalSummary"
        >
          <CardHeader className="bg-lightest text-left">
            <div
              style={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
              }}
            >
              <h2>{t("sideNav.goals")}</h2>
              {isExpand ? (
                <Icon.ExpandLess onClick={() => setIsExpand(false)} />
              ) : (
                <Icon.ExpandMore onClick={() => setIsExpand(true)} />
              )}
            </div>
          </CardHeader>
          <CardBody className="padding-top-3">
            <ul style={{ listStyleType: "none" }}>
              {data.map((goalData, index) => {
                return (
                  <React.Fragment key={index}>
                    <GoalIndividualCard
                      name={goalData.name}
                      goalAmount={goalData.goalAmount}
                      amountSaved={goalData.amountSaved}
                      isExpanded={isExpand}
                    />
                  </React.Fragment>
                );
              })}
            </ul>
          </CardBody>
          {/* <CardFooter>
        if needed footer goes here
      </CardFooter> */}
        </Card>
      );
    } else {
      return (
        <>
          <Card
            headerFirst
            //gridLayout={{ tablet: { col: 6 }, desktop: { col: "fill" } }}
            className="goalSummary"
          >
            <CardHeader className="bg-lightest text-left">
              <h2>Goals</h2>
            </CardHeader>
            <CardBody className="padding-top-3">
              <Link style={{ textDecoration: "none" }} to={"/goals"}>
                <Button
                  type={"button"}
                  className="bg-mint"
                  style={{
                    padding: 15,
                    borderRadius: "10px",
                    display: "flex",
                    margin: "auto",
                  }}
                >
                  Click here to start adding goals!
                </Button>
              </Link>
            </CardBody>
            {/* <CardFooter>
        if needed footer goes here
      </CardFooter> */}
          </Card>
        </>
      );
    }
  }
}
