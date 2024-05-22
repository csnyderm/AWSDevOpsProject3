import React, { useState } from "react";
import {
  Button,
  Card,
  CardBody,
  CardHeader,
  Table,
} from "@trussworks/react-uswds";
import { useGetPlannerByEmailQuery } from "../../app/api/plannerApi";
import { useSelector } from "react-redux";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";

interface Expense {
  category: string;
  expenseName: string;
  actualExpense: number;
  desiredExpense: number;
  dueDate: string;
}

interface Income {
  monthlyIncome: number;
}

interface ExpenseData {
  _id: string;
  categories: string[];
  expenses: Expense[];
  monthlyIncome: number;
}

interface ExpenseSummaryCardProps {
  data: ExpenseData;
}

export default function ExpenseSummaryCard() {
  const defaultExpenseData = {
    _id: "",
    categories: [],
    expenses: [],
    monthlyIncome: 0,
  };

  const email = useSelector((state: any) => state.user.email);
  const { data, error, isLoading, isSuccess } =
    useGetPlannerByEmailQuery(email);

  const { t } = useTranslation();

  const { categories, expenses, monthlyIncome } = data || defaultExpenseData;

  const [expandedCategories, setExpandedCategories] = useState<string[]>([]);

  // Function to toggle category expansion
  const toggleCategory = (category: string) => {
    if (expandedCategories.includes(category)) {
      setExpandedCategories(expandedCategories.filter((c) => c !== category));
    } else {
      setExpandedCategories([...expandedCategories, category]);
    }
  };

  const totalExpense = (expenses as Expense[]).reduce(
    (acc: number, expense: Expense) => acc + expense.actualExpense,
    0
  );

  if (!isLoading && data != null && data.categories.length != 0) {
    return (
      <Card
        headerFirst
        className="goalSummary"
        containerProps={{
          className: "text-center margin-top-2",
        }}
      >
        <CardHeader className="bg-lightest text-left">
          <h2>{t("dashboard.budgets")}</h2>
          <h3>
            {" "}
            <div>{t("dashboard.budgets")}</div>
            <span className="text-mint">
              $ {totalExpense} / $ {monthlyIncome}
            </span>
          </h3>
        </CardHeader>
        <CardBody className="padding-top-3">
          <Table bordered={false}>
            <tbody>
              {categories.map((category, categoryIndex) => (
                <React.Fragment key={categoryIndex}>
                  {/* Category */}
                  <tr
                    style={{ cursor: "pointer" }}
                    onClick={() => toggleCategory(category)}
                  >
                    <td style={{ width: "100%", fontWeight: "bold" }}>
                      {category}
                    </td>
                    <td
                      style={{ fontWeight: "bold" }}
                      className="text-right text-mint"
                    >
                      {/* Calculate total expense for this category */}$
                      {expenses
                        .filter((expense) => expense.category === category)
                        .reduce(
                          (acc, expense) => acc + expense.actualExpense,
                          0
                        )}
                    </td>
                  </tr>
                  {/* Render expenses for this category if expanded */}
                  {expandedCategories.includes(category) &&
                    expenses.map(
                      (expense, expenseIndex) =>
                        expense.category === category && (
                          <tr key={expenseIndex}>
                            <td colSpan={2} style={{ marginLeft: 10 }}>
                              {expense.expenseName}:{" "}
                              <span
                                style={{ fontWeight: "bold" }}
                                className="text-mint"
                              >
                                $ {expense.actualExpense}
                              </span>
                            </td>
                          </tr>
                        )
                    )}
                </React.Fragment>
              ))}
            </tbody>
          </Table>
        </CardBody>
      </Card>
    );
  } else
    return (
      <Card
        headerFirst
        className="goalSummary"
        //gridLayout={{ tablet: { col: 6}, desktop: { col: 2} }}
        containerProps={{
          className: "text-center margin-top-2",
        }}
      >
        <CardHeader className="bg-lightest text-left">
          <h2>{t("sideNav.budget")}</h2>
        </CardHeader>
        <CardBody className="padding-top-3">
          <Link style={{ textDecoration: "none" }} to={"/budget"}>
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
              {t("taxStarter.getStarted")}
            </Button>
          </Link>
        </CardBody>
        {/* <CardFooter>
    if needed footer goes here
  </CardFooter> */}
      </Card>
    );
}
