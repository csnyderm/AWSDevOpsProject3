import { CardGroup } from "@trussworks/react-uswds";
import AccountSummaryCard from "../components/summaries/AccountSummaryCard";
import GoalsSummaryTable from "../components/summaries/GoalsSummaryTable";
import "../styles/dashboardPosition.css";
import BudgetSummaryCard from "../components/summaries/BudgetSummaryCard";
import TaxSummaryTable from "../components/summaries/TaxSummaryTable";

import IndividualMetricCard from "../components/investments/InvestmentMetricCard";
import { useTranslation } from "react-i18next";

export default function Dashboard() {
  const { t } = useTranslation();

  return (
    <>
      <h1 style={{ textAlign: "center" }}>{t("dashboard.welcome")}</h1>
      <div className="dashboard-container">
        <div className="resize-handler account-container">
          <CardGroup className="flex-column">
            <AccountSummaryCard />
            <BudgetSummaryCard />
          </CardGroup>
        </div>
        <div className="resize-handler goal-container">
          <CardGroup className="flex-column">
            <GoalsSummaryTable />
            <IndividualMetricCard />
            <TaxSummaryTable />
          </CardGroup>
        </div>
      </div>
    </>
  );
}
