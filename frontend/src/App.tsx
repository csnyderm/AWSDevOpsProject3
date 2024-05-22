import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import Dashboard from "./pages/Dashboard";
import Account from "./pages/AccountPage";
import Goals from "./pages/Goals";
import LeftNav from "./components/LeftNav";
import TopNav from "./components/TopNav";
import TaxIncomePage from "./pages/TaxIncomePage";
import TaxStarterPage from "./pages/TaxStarterPage";
import TaxDeductionsCreditsPage from "./pages/TaxDeductionsCreditsPage";
import TaxPersonalInfoPage from "./pages/TaxPersonalInfoPage";
import Landing from "./pages/Landing";
import Investments from "./pages/Investments";
import { useSelector } from "react-redux";
import TaxOverview from "./pages/TaxOverview";
import PlannerPage from "./pages/Planner";

function App() {
  const auth = useSelector((state: any) => state.user.isLoggedIn);
  console.log(auth);

  return (
    <>
      <BrowserRouter>
        <LeftNav>
          <TopNav />
          <Routes>
            <Route path="/home" element={<Landing />} />
            <Route
              path="/"
              element={auth ? <Dashboard /> : <Navigate to="/home" />}
            />
            <Route
              path="/goals"
              element={auth ? <Goals /> : <Navigate to="/home" />}
            />
            <Route
              path="/budget"
              element={auth ? <PlannerPage /> : <Navigate to="/home" />}
            />
            <Route
              path="/account"
              element={auth ? <Account /> : <Navigate to="/home" />}
            />
            <Route
              path="/investments"
              element={auth ? <Investments /> : <Navigate to="/home" />}
            />
            <Route
              path="/tax-starter"
              element={auth ? <TaxStarterPage /> : <Navigate to="/home" />}
            />
            <Route
              path="/tax-personal-info"
              element={auth ? <TaxPersonalInfoPage /> : <Navigate to="/home" />}
            />
            <Route
              path="/tax-income"
              element={auth ? <TaxIncomePage /> : <Navigate to="/home" />}
            />
            <Route
              path="/tax-deductions"
              element={
                auth ? <TaxDeductionsCreditsPage /> : <Navigate to="/home" />
              }
            />
            <Route
              path="/tax-overview"
              element={auth ? <TaxOverview /> : <Navigate to="/home" />}
            />
          </Routes>
        </LeftNav>
      </BrowserRouter>
    </>
  );
}

export default App;
