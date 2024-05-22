import { Button, Card, CardBody, CardHeader } from "@trussworks/react-uswds";
import { useSelector } from "react-redux";
import { selectUserEmail } from "../../app/features/userSlice";
import { Link, Navigate } from "react-router-dom";
import "../../styles/goalCard.css";
import TaxOverviewForm from "../tax/forms/TaxOverviewForm";
import { useFindOneQuery } from "../../app/api/taxApi";
import { useTranslation } from "react-i18next";

export default function TaxSummaryTable() {
  const userEmail = useSelector(selectUserEmail);

  const { data, isLoading, isSuccess } = useFindOneQuery(userEmail);
  const totalIncome = data?.totalIncome || 0;
  console.log(data?.totalIncome);
  const { t } = useTranslation();

  if (!isLoading && totalIncome > 0) {
    return (
      <Card
        headerFirst
        //gridLayout={{ tablet: { col: 6 }, desktop: { col: "fill" } }}
        className="goalSummary"
      >
        <CardHeader className="bg-lightest text-left">
          <h2>{t("taxOverview.taxBreakdown")}</h2>
        </CardHeader>
        <CardBody className="padding-top-3">
          <TaxOverviewForm />
        </CardBody>
        <Link style={{ textDecoration: "none" }} to={"/tax-overview"}>
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
            {t("dashboard.vfr")}
          </Button>
        </Link>
      </Card>
    );
  } else
    return (
      <Card
        headerFirst
        //gridLayout={{ tablet: { col: 6 }, desktop: { col: "fill" } }}
        className="goalSummary"
      >
        <CardHeader className="bg-lightest text-left">
          <h2>{t("taxOverview.taxBreakdown")}</h2>
        </CardHeader>
        <CardBody className="padding-top-3">
          <Link style={{ textDecoration: "none" }} to={"/tax-starter"}>
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
              Click here to get started on your taxes!
            </Button>
          </Link>
        </CardBody>
        {/* <CardFooter>
        if needed footer goes here
      </CardFooter> */}
      </Card>
    );
}
